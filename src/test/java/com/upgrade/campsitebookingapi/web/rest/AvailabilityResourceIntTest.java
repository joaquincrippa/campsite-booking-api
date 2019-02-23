package com.upgrade.campsitebookingapi.web.rest;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upgrade.campsitebookingapi.CampsiteBookingApplication;
import com.upgrade.campsitebookingapi.domain.Availability;
import com.upgrade.campsitebookingapi.repository.AvailabilityRepository;
import com.upgrade.campsitebookingapi.service.AvailabilityService;
import com.upgrade.campsitebookingapi.web.rest.mapper.AvailabilityMapper;

/**
 * Integration Test class for the AvailabilityResource REST controller.
 *
 * @see AvailabilityResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CampsiteBookingApplication.class)
public class AvailabilityResourceIntTest {

	private static final Integer VALUE = 10;
   
	private static final LocalDate DATE = LocalDate.now().plusDays(5);
    
	ObjectMapper mapper = new ObjectMapper();
	
	@Autowired
	private AvailabilityRepository availabilityRepository;
	
	@Autowired
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;
	
	@Autowired
	private AvailabilityService availabilityService;
	
	@Autowired
	private AvailabilityMapper availabilityMapper;
	
	@Autowired
	private Validator validator;
	
	private MockMvc restAvailabilityMockMvc;
	
	private Availability availability;
	
	@Before
	public void setup() {
	    MockitoAnnotations.initMocks(this);
	    final AvailabilityResource availabilityResource =
	    		new AvailabilityResource(availabilityService, availabilityMapper);
	    this.restAvailabilityMockMvc = MockMvcBuilders.standaloneSetup(availabilityResource)
	        .setCustomArgumentResolvers(pageableArgumentResolver)
	        .setValidator(validator).build();
	}
	
	public static Availability createEntity() {
		Availability availability = new Availability();
		availability.setDate(DATE);
		availability.setValue(VALUE);
	    return availability;
	}
	
	@Before
	public void initTest() {
	    availability = createEntity();
	}	    
	
	@Test
	@Transactional
	public void getAllTasks() throws Exception {
	    availabilityRepository.saveAndFlush(availability);
	    restAvailabilityMockMvc.perform(get("/api/availabilities?sort=id,desc"))
	        .andExpect(status().isOk())
	        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
	        .andExpect(jsonPath("$.[*].value").value(hasItem(VALUE)));
	}
}
