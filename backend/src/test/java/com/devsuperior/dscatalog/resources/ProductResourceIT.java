package com.devsuperior.dscatalog.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductResourceIT {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	
	private long existingId;
	private long nonExistingId;
	private long countTotalProducts;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		countTotalProducts = 25L;
	}
	
	@Test
	public void findAllShouldReturnSortedPageWhenSortByName() throws Exception {
		mockMvc.perform(get("/products?page=0&size=12&sort=name,asc")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.totalElements").value(countTotalProducts))
		.andExpect(jsonPath("$.content").exists())
		.andExpect(jsonPath("$.content[0]").value("Macbook Pro"))
		.andExpect(jsonPath("$.content[1]").value("PC Gamer"));
	}
	
	@Test
	public void updateShouldReturnProductDTOWhenIdExists() throws Exception {
		ProductDTO productDTO = Factory.createProductDTO();
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		String expectedName = productDTO.getName();
		
		mockMvc.perform(put("/products/{id}", existingId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id").value(existingId))
		.andExpect(jsonPath("$.name").value(expectedName));
	}
	
	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
		ProductDTO productDTO = Factory.createProductDTO();
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		mockMvc.perform(put("/products/{id}", nonExistingId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound());
	}
}
