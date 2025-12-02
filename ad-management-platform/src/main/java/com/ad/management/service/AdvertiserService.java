package com.ad.management.service;

import com.ad.management.model.Advertiser;
import com.ad.management.repository.AdvertiserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AdvertiserService {
    
    private final AdvertiserRepository advertiserRepository;
    
    public AdvertiserService(AdvertiserRepository advertiserRepository) {
        this.advertiserRepository = advertiserRepository;
    }
    
    public Page<Advertiser> getAllAdvertisers(String name, Pageable pageable) {
        return advertiserRepository.findByNameContainingIgnoreCase(name, pageable);
    }
    
    public Advertiser createAdvertiser(Advertiser advertiser) {
        return advertiserRepository.save(advertiser);
    }
    
    public Optional<Advertiser> getAdvertiserById(Long id) {
        return advertiserRepository.findById(id);
    }
    
    public Advertiser updateAdvertiser(Long id, Advertiser advertiserDetails) {
        Advertiser advertiser = advertiserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("广告商未找到"));
        
        advertiser.setName(advertiserDetails.getName());
        advertiser.setContact(advertiserDetails.getContact());
        advertiser.setEmail(advertiserDetails.getEmail());
        advertiser.setStatus(advertiserDetails.getStatus());
        
        return advertiserRepository.save(advertiser);
    }
    
    public void deleteAdvertiser(Long id) {
        Advertiser advertiser = advertiserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("广告商未找到"));
        advertiserRepository.delete(advertiser);
    }
}