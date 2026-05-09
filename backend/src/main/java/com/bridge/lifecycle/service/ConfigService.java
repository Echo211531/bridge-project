package com.bridge.lifecycle.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bridge.lifecycle.dto.ConfigUpdateDTO;
import com.bridge.lifecycle.entity.SysConfig;
import com.bridge.lifecycle.mapper.ConfigMapper;
import com.bridge.lifecycle.vo.ConfigVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统配置服务类
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigService {

    private final ConfigMapper configMapper;

    /**
     * 配置查询
     *
     * @return 配置列表
     */
    public List<ConfigVO> listConfigs() {
        List<SysConfig> configs = configMapper.selectList(
                new LambdaQueryWrapper<SysConfig>()
        );
        return configs.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 根据键查询配置
     *
     * @param key 配置键
     * @return 配置信息
     */
    public ConfigVO getConfigByKey(String key) {
        SysConfig config = configMapper.selectOne(
                new LambdaQueryWrapper<SysConfig>()
                        .eq(SysConfig::getConfigKey, key)
        );
        if (config == null) {
            return null;
        }
        return convertToVO(config);
    }

    /**
     * TCO阈值配置更新
     *
     * @param updateDTO 配置更新请求
     * @return 配置信息
     */
    @Transactional(rollbackFor = Exception.class)
    public ConfigVO updateConfig(ConfigUpdateDTO updateDTO) {
        // 值范围约束校验
        validateConfigValue(updateDTO.getConfigKey(), updateDTO.getConfigValue());

        SysConfig config = configMapper.selectOne(
                new LambdaQueryWrapper<SysConfig>()
                        .eq(SysConfig::getConfigKey, updateDTO.getConfigKey())
        );

        if (config == null) {
            // 新建配置
            config = new SysConfig();
            config.setConfigKey(updateDTO.getConfigKey());
            config.setConfigValue(updateDTO.getConfigValue());
            config.setDescription(getConfigDescription(updateDTO.getConfigKey()));
            configMapper.insert(config);
        } else {
            // 更新配置
            config.setConfigValue(updateDTO.getConfigValue());
            configMapper.updateById(config);
        }

        log.info("配置更新: {} = {}", updateDTO.getConfigKey(), updateDTO.getConfigValue());
        return convertToVO(config);
    }

    /**
     * 配置值范围约束校验
     */
    private void validateConfigValue(String key, String value) {
        try {
            BigDecimal numValue = new BigDecimal(value);

            // 残值警戒线：0-50%
            if ("residual_threshold".equals(key)) {
                if (numValue.compareTo(BigDecimal.ZERO) < 0 || numValue.compareTo(new BigDecimal("50")) > 0) {
                    throw new RuntimeException("残值警戒线范围: 0-50%");
                }
            }

            // 修复警戒线：0-100%
            if ("repair_threshold".equals(key)) {
                if (numValue.compareTo(BigDecimal.ZERO) < 0 || numValue.compareTo(new BigDecimal("100")) > 0) {
                    throw new RuntimeException("修复警戒线范围: 0-100%");
                }
            }

            // 寿命警戒线：50-100%
            if ("life_threshold".equals(key)) {
                if (numValue.compareTo(new BigDecimal("50")) < 0 || numValue.compareTo(new BigDecimal("100")) > 0) {
                    throw new RuntimeException("寿命警戒线范围: 50-100%");
                }
            }
        } catch (NumberFormatException e) {
            // 非数值型配置，不做校验
        }
    }

    /**
     * 获取配置描述
     */
    private String getConfigDescription(String key) {
        switch (key) {
            case "residual_threshold":
                return "残值警戒线(%): 设备残值低于此阈值进入待鉴定";
            case "repair_threshold":
                return "修复警戒线(%): 单次维修费用占购置成本超过此阈值进入待鉴定";
            case "life_threshold":
                return "寿命警戒线(%): 设备使用年限超过设计寿命此比例进入待鉴定";
            case "fault_threshold":
                return "故障次数警戒线: 累计故障次数超过此阈值进入待鉴定";
            default:
                return "";
        }
    }

    /**
     * 转换实体为VO
     */
    private ConfigVO convertToVO(SysConfig config) {
        ConfigVO vo = new ConfigVO();
        vo.setId(config.getId());
        vo.setConfigKey(config.getConfigKey());
        vo.setConfigValue(config.getConfigValue());
        vo.setDescription(config.getDescription());
        vo.setCreateTime(config.getCreateTime());
        vo.setUpdateTime(config.getUpdateTime());
        return vo;
    }
}