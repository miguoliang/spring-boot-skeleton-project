package com.muchencute.biz.service;

import com.muchencute.biz.model.Setting;
import com.muchencute.biz.repository.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingService {

  private final SettingRepository settingRepository;

  @Autowired
  public SettingService(SettingRepository settingRepository) {

    this.settingRepository = settingRepository;
  }

  public String getOrDefault(String key, String defaultValue) {

    final var setting = settingRepository.findById(key);
    return setting.isPresent() ? setting.get().getValue() : defaultValue;
  }

  public void put(String key, String value) {

    final var setting = settingRepository.findById(key).orElse(new Setting());
    setting.setKey(key);
    setting.setValue(value);
    settingRepository.saveAndFlush(setting);
  }
}
