package com.mc.ibpts.paymentapp;

import com.mc.ibpts.paymentapp.dvo.AccountInfo;
import com.mc.ibpts.paymentapp.dvo.TransactionInfo;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@EnableAutoConfiguration
@SpringBootApplication
public class IntraPaymentAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(IntraPaymentAppApplication.class, args);
	}

	@Bean
	public CacheManager cacheManager() {
		CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
				.withCache("accounts", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, AccountInfo.class, ResourcePoolsBuilder.heap(50)))
				.withCache("transactions", CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, TransactionInfo.class, ResourcePoolsBuilder.heap(100)))
				.withCache("idempotency", CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.heap(100)))
				.build();
		cacheManager.init();
		return cacheManager;
	}

}
