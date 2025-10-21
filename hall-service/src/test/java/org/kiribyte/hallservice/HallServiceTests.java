package org.kiribyte.hallservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kiribyte.hallservice.dto.AddHallRequest;
import org.kiribyte.hallservice.dto.HallDto;
import org.kiribyte.hallservice.dto.HallWithSeatsDto;
import org.kiribyte.hallservice.entity.HallEntity;
import org.kiribyte.hallservice.entity.SeatEntity;
import org.kiribyte.hallservice.exception.HallNotFoundException;
import org.kiribyte.hallservice.mapper.HallMapper;
import org.kiribyte.hallservice.repository.HallRepository;
import org.kiribyte.hallservice.repository.SeatRepository;
import org.kiribyte.hallservice.service.impl.HallServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HallServiceTests {

    @Mock
    private HallRepository hallRepository;

    @Mock
    private HallMapper hallMapper;

    @Mock
    private SeatRepository seatRepository;

    @InjectMocks
    private HallServiceImpl hallService;

    private HallEntity hallEntity;
    private HallDto hallDto;
    private AddHallRequest addHallRequest;
    private HallWithSeatsDto hallWithSeatsDto;
    private List<SeatEntity> seatEntities;

    @BeforeEach
    void setUp() {
        // Arrange - создание тестовых данных
        hallEntity = new HallEntity();
        hallEntity.setId(1L);
        hallEntity.setName("Зал 1");
        hallEntity.setDescription("Большой зал");

        hallDto = new HallDto();
        hallDto.setId(1L);
        hallDto.setName("Зал 1");
        hallDto.setDescription("Большой зал");

        addHallRequest = new AddHallRequest();
        addHallRequest.setName("Новый зал");
        addHallRequest.setDescription("Описание нового зала");

        hallWithSeatsDto = new HallWithSeatsDto();
        hallWithSeatsDto.setId(1L);
        hallWithSeatsDto.setName("Зал 1");
        hallWithSeatsDto.setDescription("Большой зал");

        seatEntities = Arrays.asList(new SeatEntity(), new SeatEntity());
    }

    @Test
    void getAllHalls_ShouldReturnListOfHalls_WhenHallsExist() {
        // Arrange
        List<HallEntity> hallEntities = Arrays.asList(hallEntity);
        List<HallDto> expectedHalls = Arrays.asList(hallDto);
        
        when(hallRepository.findAll()).thenReturn(hallEntities);
        when(hallMapper.entitiesToDtos(hallEntities)).thenReturn(expectedHalls);

        // Act
        List<HallDto> result = hallService.getAllHalls();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Зал 1", result.get(0).getName());
        verify(hallRepository).findAll();
        verify(hallMapper).entitiesToDtos(hallEntities);
    }

    @Test
    void getHallById_ShouldReturnHall_WhenHallExists() {
        // Arrange
        Long hallId = 1L;
        when(hallRepository.findById(hallId)).thenReturn(Optional.of(hallEntity));
        when(hallMapper.entityToDto(hallEntity)).thenReturn(hallDto);

        // Act
        HallDto result = hallService.getHallById(hallId);

        // Assert
        assertNotNull(result);
        assertEquals(hallId, result.getId());
        assertEquals("Зал 1", result.getName());
        verify(hallRepository).findById(hallId);
        verify(hallMapper).entityToDto(hallEntity);
    }

    @Test
    void getHallWithSeatsById_ShouldReturnHallWithSeats_WhenHallExists() {
        // Arrange
        Long hallId = 1L;
        when(hallRepository.findHallWithSeatsAndTypeById(hallId)).thenReturn(Optional.of(hallEntity));
        when(hallMapper.entityToDtoWithSeats(hallEntity)).thenReturn(hallWithSeatsDto);

        // Act
        HallWithSeatsDto result = hallService.getHallWithSeatsById(hallId);

        // Assert
        assertNotNull(result);
        assertEquals(hallId, result.getId());
        assertEquals("Зал 1", result.getName());
        verify(hallRepository).findHallWithSeatsAndTypeById(hallId);
        verify(hallMapper).entityToDtoWithSeats(hallEntity);
    }

    @Test
    void addHall_ShouldReturnSavedHall_WhenValidRequestProvided() {
        // Arrange
        HallEntity savedHall = new HallEntity();
        savedHall.setId(2L);
        savedHall.setName("Новый зал");
        savedHall.setDescription("Описание нового зала");

        HallDto expectedDto = new HallDto();
        expectedDto.setId(2L);
        expectedDto.setName("Новый зал");
        expectedDto.setDescription("Описание нового зала");

        when(hallMapper.addRequestToEntity(addHallRequest)).thenReturn(hallEntity);
        when(hallRepository.save(hallEntity)).thenReturn(savedHall);
        when(hallMapper.entityToDto(savedHall)).thenReturn(expectedDto);

        // Act
        HallDto result = hallService.addHall(addHallRequest);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("Новый зал", result.getName());
        verify(hallMapper).addRequestToEntity(addHallRequest);
        verify(hallRepository).save(hallEntity);
        verify(hallMapper).entityToDto(savedHall);
    }

    @Test
    void updateHallById_ShouldReturnUpdatedHall_WhenHallExists() {
        // Arrange
        Long hallId = 1L;
        HallDto updatedHallDto = new HallDto();
        updatedHallDto.setId(hallId);
        updatedHallDto.setName("Обновленный зал");
        updatedHallDto.setDescription("Обновленное описание");

        when(hallRepository.findById(hallId)).thenReturn(Optional.of(hallEntity));
        when(hallRepository.save(hallEntity)).thenReturn(hallEntity);
        when(hallMapper.entityToDto(hallEntity)).thenReturn(updatedHallDto);

        // Act
        HallDto result = hallService.updateHallById(hallId, updatedHallDto);

        // Assert
        assertNotNull(result);
        assertEquals(hallId, result.getId());
        assertEquals("Обновленный зал", result.getName());
        verify(hallRepository).findById(hallId);
        verify(hallMapper).updateEntityFromDto(updatedHallDto, hallEntity);
        verify(hallRepository).save(hallEntity);
        verify(hallMapper).entityToDto(hallEntity);
    }

    @Test
    void deleteHallById_ShouldDeleteHallAndSeats_WhenHallExists() {
        // Arrange
        Long hallId = 1L;
        when(seatRepository.findByHallId(hallId)).thenReturn(seatEntities);

        // Act
        hallService.deleteHallById(hallId);

        // Assert
        verify(seatRepository).findByHallId(hallId);
        verify(seatRepository).deleteAll(seatEntities);
        verify(hallRepository).deleteById(hallId);
    }

    @Test
    void getHallById_ShouldThrowHallNotFoundException_WhenHallDoesNotExist() {
        // Arrange
        Long nonExistentHallId = 999L;
        when(hallRepository.findById(nonExistentHallId)).thenReturn(Optional.empty());

        // Act & Assert
        HallNotFoundException exception = assertThrows(HallNotFoundException.class, 
            () -> hallService.getHallById(nonExistentHallId));
        
        assertEquals("Hall not found", exception.getMessage());
        verify(hallRepository).findById(nonExistentHallId);
        verify(hallMapper, never()).entityToDto(any());
    }
}
