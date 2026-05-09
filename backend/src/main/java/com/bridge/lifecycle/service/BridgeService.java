package com.bridge.lifecycle.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bridge.lifecycle.dto.AadtUpdateDTO;
import com.bridge.lifecycle.dto.BridgeDTO;
import com.bridge.lifecycle.dto.BridgeUpdateDTO;
import com.bridge.lifecycle.entity.Bridge;
import com.bridge.lifecycle.entity.BridgeAadtHistory;
import com.bridge.lifecycle.mapper.BridgeAadtHistoryMapper;
import com.bridge.lifecycle.mapper.BridgeMapper;
import com.bridge.lifecycle.vo.AadtHistoryVO;
import com.bridge.lifecycle.vo.BridgeDetailVO;
import com.bridge.lifecycle.vo.BridgeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 桥梁管理服务类
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BridgeService {

    private final BridgeMapper bridgeMapper;
    private final BridgeAadtHistoryMapper aadtHistoryMapper;

    // α气候系数映射表（5种气候带对应值）
    private static final java.util.Map<String, BigDecimal> ALPHA_COEF_MAP = java.util.Map.of(
            "cold", new BigDecimal("1.3"),           // 寒冷气候带
            "severe_cold", new BigDecimal("1.5"),    // 严寒气候带
            "temperate", new BigDecimal("1.0"),      // 温带气候带
            "humid", new BigDecimal("1.1"),          // 湿润气候带
            "coastal", new BigDecimal("1.2")         // 沿海气候带
    );

    // β AADT系数映射表（5种范围对应值）
    private static final java.util.Map<String, BigDecimal> BETA_COEF_MAP = java.util.Map.of(
            "low", new BigDecimal("1.0"),            // <5000
            "medium_low", new BigDecimal("1.1"),     // 5000-10000
            "medium", new BigDecimal("1.2"),         // 10000-20000
            "medium_high", new BigDecimal("1.3"),    // 20000-30000
            "high", new BigDecimal("1.5")            // >30000
    );

    // 气候带名称映射
    private static final java.util.Map<String, String> CLIMATE_ZONE_NAME_MAP = java.util.Map.of(
            "cold", "寒冷气候带",
            "severe_cold", "严寒气候带",
            "temperate", "温带气候带",
            "humid", "湿润气候带",
            "coastal", "沿海气候带"
    );

    /**
     * 计算α气候系数
     *
     * @param climateZone 气候带
     * @return α系数
     */
    public BigDecimal calculateAlphaCoef(String climateZone) {
        return ALPHA_COEF_MAP.getOrDefault(climateZone, BigDecimal.ONE);
    }

    /**
     * 计算β AADT系数
     *
     * @param aadt 年平均日交通量
     * @return β系数
     */
    public BigDecimal calculateBetaCoef(Integer aadt) {
        if (aadt == null) {
            return BigDecimal.ONE;
        }
        if (aadt < 5000) {
            return BETA_COEF_MAP.get("low");
        } else if (aadt < 10000) {
            return BETA_COEF_MAP.get("medium_low");
        } else if (aadt < 20000) {
            return BETA_COEF_MAP.get("medium");
        } else if (aadt < 30000) {
            return BETA_COEF_MAP.get("medium_high");
        } else {
            return BETA_COEF_MAP.get("high");
        }
    }

    /**
     * 桥梁列表查询
     *
     * @param pageNum   页码
     * @param pageSize  每页数量
     * @param bridgeName 桥梁名称（模糊查询）
     * @return 分页桥梁列表
     */
    public Page<BridgeVO> listBridges(Integer pageNum, Integer pageSize, String bridgeName) {
        Page<Bridge> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Bridge> wrapper = new LambdaQueryWrapper<>();

        // 桥梁名称模糊查询
        if (StringUtils.hasText(bridgeName)) {
            wrapper.like(Bridge::getBridgeName, bridgeName);
        }
        // 按创建时间降序
        wrapper.orderByDesc(Bridge::getCreateTime);

        Page<Bridge> bridgePage = bridgeMapper.selectPage(page, wrapper);

        // 转换为VO
        Page<BridgeVO> voPage = new Page<>(bridgePage.getCurrent(), bridgePage.getSize(), bridgePage.getTotal());
        List<BridgeVO> voList = bridgePage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    /**
     * 桥梁详情查询（含AADT历史）
     *
     * @param id 桥梁ID
     * @return 桥梁详情
     */
    public BridgeDetailVO getBridgeDetail(String id) {
        Bridge bridge = bridgeMapper.selectById(id);
        if (bridge == null) {
            throw new RuntimeException("桥梁不存在");
        }

        BridgeDetailVO vo = convertToDetailVO(bridge);

        // 查询AADT历史记录
        List<BridgeAadtHistory> historyList = aadtHistoryMapper.selectList(
                new LambdaQueryWrapper<BridgeAadtHistory>()
                        .eq(BridgeAadtHistory::getBridgeId, id)
                        .orderByDesc(BridgeAadtHistory::getChangeTime)
        );
        List<AadtHistoryVO> historyVOList = historyList.stream()
                .map(this::convertAadtHistoryToVO)
                .collect(Collectors.toList());
        vo.setAadtHistory(historyVOList);

        return vo;
    }

    /**
     * 创建桥梁
     *
     * @param bridgeDTO 桥梁创建请求
     * @return 桥梁信息
     */
    @Transactional(rollbackFor = Exception.class)
    public BridgeVO createBridge(BridgeDTO bridgeDTO) {
        // 桥梁编码唯一校验
        Bridge existBridge = bridgeMapper.selectOne(
                new LambdaQueryWrapper<Bridge>()
                        .eq(Bridge::getBridgeCode, bridgeDTO.getBridgeCode())
        );
        if (existBridge != null) {
            throw new RuntimeException("桥梁编码已存在");
        }

        // 创建桥梁实体
        Bridge bridge = new Bridge();
        bridge.setBridgeCode(bridgeDTO.getBridgeCode());
        bridge.setBridgeName(bridgeDTO.getBridgeName());
        bridge.setClimateZone(bridgeDTO.getClimateZone());
        bridge.setAadt(bridgeDTO.getAadt());
        bridge.setLocation(bridgeDTO.getLocation());
        bridge.setBuildYear(bridgeDTO.getBuildYear());
        bridge.setLength(bridgeDTO.getLength());

        // 计算α和β系数
        bridge.setAlphaCoef(calculateAlphaCoef(bridgeDTO.getClimateZone()));
        bridge.setBetaCoef(calculateBetaCoef(bridgeDTO.getAadt()));

        bridgeMapper.insert(bridge);
        log.info("创建桥梁成功: {}", bridge.getBridgeCode());

        return convertToVO(bridge);
    }

    /**
     * 更新桥梁
     *
     * @param bridgeUpdateDTO 桥梁更新请求
     * @return 桥梁信息
     */
    @Transactional(rollbackFor = Exception.class)
    public BridgeVO updateBridge(BridgeUpdateDTO bridgeUpdateDTO) {
        Bridge bridge = bridgeMapper.selectById(bridgeUpdateDTO.getId());
        if (bridge == null) {
            throw new RuntimeException("桥梁不存在");
        }

        // 更新字段
        if (StringUtils.hasText(bridgeUpdateDTO.getBridgeName())) {
            bridge.setBridgeName(bridgeUpdateDTO.getBridgeName());
        }
        if (StringUtils.hasText(bridgeUpdateDTO.getClimateZone())) {
            bridge.setClimateZone(bridgeUpdateDTO.getClimateZone());
            bridge.setAlphaCoef(calculateAlphaCoef(bridgeUpdateDTO.getClimateZone()));
        }
        if (StringUtils.hasText(bridgeUpdateDTO.getLocation())) {
            bridge.setLocation(bridgeUpdateDTO.getLocation());
        }
        if (bridgeUpdateDTO.getBuildYear() != null) {
            bridge.setBuildYear(bridgeUpdateDTO.getBuildYear());
        }
        if (bridgeUpdateDTO.getLength() != null) {
            bridge.setLength(bridgeUpdateDTO.getLength());
        }

        bridgeMapper.updateById(bridge);
        log.info("更新桥梁成功: {}", bridge.getBridgeCode());

        return convertToVO(bridge);
    }

    /**
     * 更新AADT（记录历史变更）
     *
     * @param aadtUpdateDTO AADT更新请求
     * @return 桥梁信息
     */
    @Transactional(rollbackFor = Exception.class)
    public BridgeVO updateAadt(AadtUpdateDTO aadtUpdateDTO) {
        Bridge bridge = bridgeMapper.selectById(aadtUpdateDTO.getBridgeId());
        if (bridge == null) {
            throw new RuntimeException("桥梁不存在");
        }

        // 保存历史记录
        BridgeAadtHistory history = new BridgeAadtHistory();
        history.setBridgeId(bridge.getId());
        history.setOldAadt(bridge.getAadt());
        history.setNewAadt(aadtUpdateDTO.getNewAadt());
        history.setOldBetaCoef(bridge.getBetaCoef());
        BigDecimal newBetaCoef = calculateBetaCoef(aadtUpdateDTO.getNewAadt());
        history.setNewBetaCoef(newBetaCoef);
        history.setReason(aadtUpdateDTO.getReason());
        aadtHistoryMapper.insert(history);

        // 更新桥梁AADT和β系数
        bridge.setAadt(aadtUpdateDTO.getNewAadt());
        bridge.setBetaCoef(newBetaCoef);
        bridgeMapper.updateById(bridge);

        log.info("更新AADT成功: 桥梁={}, 旧AADT={}, 新AADT={}",
                bridge.getBridgeCode(), history.getOldAadt(), history.getNewAadt());

        return convertToVO(bridge);
    }

    /**
     * 查询AADT历史
     *
     * @param bridgeId 桥梁ID
     * @return AADT历史记录列表
     */
    public List<AadtHistoryVO> getAadtHistory(String bridgeId) {
        List<BridgeAadtHistory> historyList = aadtHistoryMapper.selectList(
                new LambdaQueryWrapper<BridgeAadtHistory>()
                        .eq(BridgeAadtHistory::getBridgeId, bridgeId)
                        .orderByDesc(BridgeAadtHistory::getChangeTime)
        );
        return historyList.stream()
                .map(this::convertAadtHistoryToVO)
                .collect(Collectors.toList());
    }

    /**
     * 转换实体为VO
     */
    private BridgeVO convertToVO(Bridge bridge) {
        BridgeVO vo = new BridgeVO();
        vo.setId(bridge.getId());
        vo.setBridgeCode(bridge.getBridgeCode());
        vo.setBridgeName(bridge.getBridgeName());
        vo.setClimateZone(bridge.getClimateZone());
        vo.setClimateZoneName(CLIMATE_ZONE_NAME_MAP.getOrDefault(bridge.getClimateZone(), ""));
        vo.setAlphaCoef(bridge.getAlphaCoef());
        vo.setAadt(bridge.getAadt());
        vo.setBetaCoef(bridge.getBetaCoef());
        vo.setLocation(bridge.getLocation());
        vo.setBuildYear(bridge.getBuildYear());
        vo.setLength(bridge.getLength());
        vo.setCreateTime(bridge.getCreateTime());
        vo.setUpdateTime(bridge.getUpdateTime());
        return vo;
    }

    /**
     * 转换实体为详情VO
     */
    private BridgeDetailVO convertToDetailVO(Bridge bridge) {
        BridgeDetailVO vo = new BridgeDetailVO();
        vo.setId(bridge.getId());
        vo.setBridgeCode(bridge.getBridgeCode());
        vo.setBridgeName(bridge.getBridgeName());
        vo.setClimateZone(bridge.getClimateZone());
        vo.setClimateZoneName(CLIMATE_ZONE_NAME_MAP.getOrDefault(bridge.getClimateZone(), ""));
        vo.setAlphaCoef(bridge.getAlphaCoef());
        vo.setAadt(bridge.getAadt());
        vo.setBetaCoef(bridge.getBetaCoef());
        vo.setLocation(bridge.getLocation());
        vo.setBuildYear(bridge.getBuildYear());
        vo.setLength(bridge.getLength());
        vo.setCreateTime(bridge.getCreateTime());
        vo.setUpdateTime(bridge.getUpdateTime());
        return vo;
    }

    /**
     * 转换AADT历史实体为VO
     */
    private AadtHistoryVO convertAadtHistoryToVO(BridgeAadtHistory history) {
        AadtHistoryVO vo = new AadtHistoryVO();
        vo.setId(history.getId());
        vo.setBridgeId(history.getBridgeId());
        vo.setOldAadt(history.getOldAadt());
        vo.setNewAadt(history.getNewAadt());
        vo.setReason(history.getReason());
        vo.setChangeTime(history.getChangeTime());

        // β系数变更说明
        if (history.getOldBetaCoef() != null && history.getNewBetaCoef() != null) {
            if (history.getOldBetaCoef().compareTo(history.getNewBetaCoef()) < 0) {
                vo.setBetaCoefChange("β系数上升: " + history.getOldBetaCoef() + " → " + history.getNewBetaCoef());
            } else if (history.getOldBetaCoef().compareTo(history.getNewBetaCoef()) > 0) {
                vo.setBetaCoefChange("β系数下降: " + history.getOldBetaCoef() + " → " + history.getNewBetaCoef());
            } else {
                vo.setBetaCoefChange("β系数不变: " + history.getOldBetaCoef());
            }
        }
        return vo;
    }
}