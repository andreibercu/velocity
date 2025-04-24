package com.abercu.velocity.birds;

import com.abercu.velocity.birds.configuration.ElasticIndexInitializer;
import com.abercu.velocity.birds.repository.ElasticBirdRepository;
import com.abercu.velocity.birds.repository.ElasticSightingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class BirdsApplicationTests {

	@MockBean
	private ElasticBirdRepository elasticBirdRepository;

	@MockBean
	private ElasticSightingRepository elasticSightingRepository;

	@MockBean
	private ElasticIndexInitializer elasticIndexInitializer;

	@Test
	void contextLoads() {
	}
}
