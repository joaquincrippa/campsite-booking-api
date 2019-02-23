package com.upgrade.campsitebookingapi.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityManager;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upgrade.campsitebookingapi.CampsiteBookingApplication;
import com.upgrade.campsitebookingapi.domain.Availability;
import com.upgrade.campsitebookingapi.domain.Booking;
import com.upgrade.campsitebookingapi.repository.AvailabilityRepository;
import com.upgrade.campsitebookingapi.repository.BookingRepository;
import com.upgrade.campsitebookingapi.service.BookingService;
import com.upgrade.campsitebookingapi.web.rest.dto.BookingDTO;
import com.upgrade.campsitebookingapi.web.rest.mapper.BookingMapper;

/**
 * Integration Test class for the BookingResource REST controller.
 *
 * @see BookingResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CampsiteBookingApplication.class)
public class BookingResourceIntTest {
	
	private static final LocalDate DEFAULT_ARRIVAL_DATE = LocalDate.now().plusDays(5);
	private static final LocalDate UPDATED_ARRIVAL_DATE = LocalDate.now().plusDays(6);
	private static final LocalDate DEFAULT_DEPARTURE_DATE = LocalDate.now().plusDays(7);
	private static final LocalDate UPDATED_DEPARTURE_DATE = LocalDate.now().plusDays(9);
	private static final Integer DEFAULT_PEOPLE = 3;
	private static final Integer UPDATED_PEOPLE = 5;
	private static final String DEFAULT_EMAIL = "pp@mail.com";
	private static final String UPDATED_EMAIL = "rr@mail.com";
	private static final String DEFAULT_FULL_NAME = "PABLO PER";
	private static final String UPDATED_FULL_NAME = "RAUL RAMIR";
   	
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;
    
    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private Validator validator;
    
    @Autowired
    private EntityManager em;

    private MockMvc restBookingMockMvc;

    private Booking booking;
    
	private Availability av1;
	
	private Availability av2;
	
	private Availability av3;
	
	private Availability av4;
	
	private Availability av5;

	@Before
	public void setup() {
	    MockitoAnnotations.initMocks(this);
	    final BookingResource bookingResource =
	    		new BookingResource(bookingService, bookingMapper);
	    this.restBookingMockMvc = MockMvcBuilders.standaloneSetup(bookingResource)
	        .setCustomArgumentResolvers(pageableArgumentResolver)
	        .setValidator(validator).build();
	}
	
	public static Booking createEntity() {
		Booking booking = new Booking();
		booking.setArrivalDate(DEFAULT_ARRIVAL_DATE);
		booking.setDepartureDate(DEFAULT_DEPARTURE_DATE);
		booking.setEmail(DEFAULT_EMAIL);
		booking.setFullName(DEFAULT_FULL_NAME);
		booking.setPeople(DEFAULT_PEOPLE);
		return booking;
	}
	
	@Before
	public void initTest() {
	    booking = createEntity();
		av1 = AvailabilityResourceIntTest.createEntity();
		av1.setDate(LocalDate.now().plusDays(5));
		av1 = availabilityRepository.saveAndFlush(av1);
		av2 = AvailabilityResourceIntTest.createEntity();
		av2.setDate(LocalDate.now().plusDays(6));
		av2 = availabilityRepository.saveAndFlush(av2);
		av3 = AvailabilityResourceIntTest.createEntity();
		av3.setDate(LocalDate.now().plusDays(7));
		av3 = availabilityRepository.saveAndFlush(av3);
		av4 = AvailabilityResourceIntTest.createEntity();
		av4.setDate(LocalDate.now().plusDays(8));
		av4 = availabilityRepository.saveAndFlush(av4);
		av5 = AvailabilityResourceIntTest.createEntity();
		av5.setDate(LocalDate.now().plusDays(9));
		av5 = availabilityRepository.saveAndFlush(av5);
	}
	
	@Test
	@Transactional
	public void createBooking() throws JsonProcessingException, Exception {
		int databaseSizeBeforeCreate = bookingRepository.findAll().size();
		int availability1BeforeCreate = av1.getValue();
		int availability2BeforeCreate = av2.getValue();
		
		BookingDTO bookingDTO = bookingMapper.toDto(booking);
        restBookingMockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(bookingDTO)))
                .andExpect(status().isCreated());
        
        // Validate the booking
        List<Booking> bookingList = bookingRepository.findAll();
        assertThat(bookingList).hasSize(databaseSizeBeforeCreate + 1);
        Booking testBooking = bookingList.get(bookingList.size() - 1);
        assertThat(testBooking.getArrivalDate()).isEqualTo(DEFAULT_ARRIVAL_DATE.toString());
        assertThat(testBooking.getDepartureDate()).isEqualTo(DEFAULT_DEPARTURE_DATE.toString());
        assertThat(testBooking.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testBooking.getFullName()).isEqualTo(DEFAULT_FULL_NAME);
        assertThat(testBooking.getPeople()).isEqualTo(DEFAULT_PEOPLE);
        
        // Validate availability
        assertThat(availabilityRepository.getOne(av1.getId()).getValue()).isEqualTo(availability1BeforeCreate - booking.getPeople());
        assertThat(availabilityRepository.getOne(av2.getId()).getValue()).isEqualTo(availability2BeforeCreate - booking.getPeople());		
	}
	
	@Test
	@Transactional
	public void createBookingButNonAvailableCapacity() throws JsonProcessingException, Exception {
		// Availability = 10
		BookingDTO bookingDTO = bookingMapper.toDto(booking);
        restBookingMockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(bookingDTO)))
                .andExpect(status().isCreated());
        // Availability = 7
        restBookingMockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(bookingDTO)))
                .andExpect(status().isCreated());
        // Availability = 4
        restBookingMockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(bookingDTO)))
                .andExpect(status().isCreated());
        // Availability = 1
        restBookingMockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(bookingDTO)))
                .andExpect(status().isBadRequest());
        // The last test should fail
	}
	
	@Test
	@Transactional
	public void createBookingWithMoreThanThreeDaysLong() throws JsonProcessingException, Exception {
		booking.setArrivalDate(LocalDate.now());
		booking.setDepartureDate(LocalDate.now().plusDays(4));
		BookingDTO bookingDTO = bookingMapper.toDto(booking);
		
        restBookingMockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(bookingDTO)))
                .andExpect(status().isBadRequest());
	}
	
	@Test
	@Transactional
	public void createBookingWithPreviousArrivalDate() throws JsonProcessingException, Exception {
		booking.setArrivalDate(LocalDate.now().minusDays(1));
		BookingDTO bookingDTO = bookingMapper.toDto(booking);
		
        restBookingMockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(bookingDTO)))
                .andExpect(status().isBadRequest());
	}
	
	@Test
	@Transactional
	public void updateBooking() throws JsonProcessingException, Exception {
		int availability1BeforeCreate = av1.getValue();
		int availability2BeforeCreate = av2.getValue();
		int availability3BeforeCreate = av3.getValue();
		int availability4BeforeCreate = av4.getValue();
		int availability5BeforeCreate = av5.getValue();

		booking = bookingService.create(bookingMapper.toDto(booking));
		int databaseSizeBeforeUpdate = bookingRepository.findAll().size();
		
		Booking updatedBooking = bookingRepository.getOne(booking.getId());
		em.detach(updatedBooking);
		updatedBooking.setEmail(UPDATED_EMAIL);
		updatedBooking.setPeople(UPDATED_PEOPLE);
		updatedBooking.setFullName(UPDATED_FULL_NAME);
		updatedBooking.setArrivalDate(UPDATED_ARRIVAL_DATE);
		updatedBooking.setDepartureDate(UPDATED_DEPARTURE_DATE);
		BookingDTO bookingDTO = bookingMapper.toDto(updatedBooking);
		
		 restBookingMockMvc.perform(put("/api/bookings/{id}", booking.getId())
		            .contentType(MediaType.APPLICATION_JSON)
		            .content(mapper.writeValueAsBytes(bookingDTO)))
		            .andExpect(status().isOk());
		 
		// Validate the booking
		List<Booking> bookingList = bookingRepository.findAll();
		assertThat(bookingList).hasSize(databaseSizeBeforeUpdate);
		Booking testBooking = bookingList.get(bookingList.size() - 1);
		assertThat(testBooking.getArrivalDate()).isEqualTo(UPDATED_ARRIVAL_DATE.toString());
		assertThat(testBooking.getDepartureDate()).isEqualTo(UPDATED_DEPARTURE_DATE.toString());
		assertThat(testBooking.getEmail()).isEqualTo(UPDATED_EMAIL);
		assertThat(testBooking.getFullName()).isEqualTo(UPDATED_FULL_NAME);
		assertThat(testBooking.getPeople()).isEqualTo(UPDATED_PEOPLE);		 

		// Validate availability
        assertThat(availabilityRepository.getOne(av1.getId()).getValue()).isEqualTo(availability1BeforeCreate);
        assertThat(availabilityRepository.getOne(av2.getId()).getValue()).isEqualTo(availability2BeforeCreate - booking.getPeople());
        assertThat(availabilityRepository.getOne(av3.getId()).getValue()).isEqualTo(availability3BeforeCreate - booking.getPeople());
        assertThat(availabilityRepository.getOne(av4.getId()).getValue()).isEqualTo(availability4BeforeCreate - booking.getPeople());
        assertThat(availabilityRepository.getOne(av5.getId()).getValue()).isEqualTo(availability5BeforeCreate);
		
	}
	
	public void updateNonExistingBooking() throws JsonProcessingException, Exception {
		BookingDTO bookingDTO = new BookingDTO();
		bookingDTO.setEmail(UPDATED_EMAIL);
		bookingDTO.setPeople(UPDATED_PEOPLE);
		bookingDTO.setFullName(UPDATED_FULL_NAME);
		bookingDTO.setArrivalDate(UPDATED_ARRIVAL_DATE);
		bookingDTO.setDepartureDate(UPDATED_DEPARTURE_DATE);
		
		 restBookingMockMvc.perform(put("/api/bookings/-1")
		            .contentType(MediaType.APPLICATION_JSON)
		            .content(mapper.writeValueAsBytes(bookingDTO)))
		            .andExpect(status().isNotFound());		
	}
	
	@Test
	@Transactional
	public void updateActiveBooking() throws JsonProcessingException, Exception {
		booking.setArrivalDate(LocalDate.now().minusDays(1));
		bookingRepository.saveAndFlush(booking);
		
		BookingDTO bookingDTO = new BookingDTO();
		bookingDTO.setEmail(UPDATED_EMAIL);
		bookingDTO.setPeople(UPDATED_PEOPLE);
		bookingDTO.setFullName(UPDATED_FULL_NAME);
		bookingDTO.setArrivalDate(UPDATED_ARRIVAL_DATE);
		bookingDTO.setDepartureDate(UPDATED_DEPARTURE_DATE);
		
		restBookingMockMvc.perform(put("/api/bookings/{id}", booking.getId())
		            .contentType(MediaType.APPLICATION_JSON)
		            .content(mapper.writeValueAsBytes(bookingDTO)))
		            .andExpect(status().isBadRequest());		
	}
	
	@Test
	@Transactional
	public void cancelBooking() throws Exception {
		booking = bookingService.create(bookingMapper.toDto(booking));
		int availability1BeforeDelete = av1.getValue();
		int availability2BeforeDelete = av2.getValue();
		int databaseSizeBeforeDelete = bookingRepository.findAll().size();
		
	    // Delete the booking
        restBookingMockMvc.perform(delete("/api/bookings/{id}", booking.getId())
	            .contentType(MediaType.APPLICATION_JSON))
	            .andExpect(status().isOk());
        
        // Validate not found booking
        assertThat(bookingRepository.findById(booking.getId())).isEmpty();
        assertThat(bookingRepository.findAll()).hasSize(databaseSizeBeforeDelete - 1);
    
        // Validate availability
        assertThat(availabilityRepository.getOne(av1.getId()).getValue()).isEqualTo(availability1BeforeDelete + booking.getPeople());
        assertThat(availabilityRepository.getOne(av2.getId()).getValue()).isEqualTo(availability2BeforeDelete + booking.getPeople());
    
	}
	
	@Test
	@Transactional
	public void cancelNonExistingBooking() throws Exception {
        restBookingMockMvc.perform(delete("/api/bookings/{id}", -1)
	            .contentType(MediaType.APPLICATION_JSON))
	            .andExpect(status().isNotFound());		
	}
	
	@Test
	@Transactional
	public void cancelActiveBooking() throws Exception {
		booking.setArrivalDate(LocalDate.now().minusDays(1));
		bookingRepository.saveAndFlush(booking);

		restBookingMockMvc.perform(delete("/api/bookings/{id}", booking.getId())
	            .contentType(MediaType.APPLICATION_JSON))
	            .andExpect(status().isBadRequest());
	}

}
