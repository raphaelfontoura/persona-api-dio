package one.digitalinnovation.personapi.service;

import one.digitalinnovation.personapi.dto.request.PersonDTO;
import one.digitalinnovation.personapi.dto.response.MessageResponseDTO;
import one.digitalinnovation.personapi.entity.Person;
import one.digitalinnovation.personapi.exception.PersonNotFoundException;
import one.digitalinnovation.personapi.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static one.digitalinnovation.personapi.utils.PersonUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PersonService personService;

    @Test
    void testGivenPersonDTOThenReturnSavedMessage() {
        PersonDTO personDTO = createFakeDTO();
        Person expectedSavedPerson = createFakeEntity();

        when(personRepository.save(any(Person.class))).thenReturn(expectedSavedPerson);

        MessageResponseDTO expectedSuccessMessage = createExpectedMessageResponse(expectedSavedPerson.getId(),"Created person with ID");
        MessageResponseDTO succesMessage = personService.createPerson(personDTO);

        assertEquals(expectedSuccessMessage, succesMessage);
    }

    @Test
    void testGetAllPersonThenReturnListPersonDTO() {
        List<Person> persons = new ArrayList<>(){{
            add(createFakeEntity());
        }};
        when(personRepository.findAll()).thenReturn(persons);

        List<PersonDTO> expectedPersonDTOList = createFakeDTOList();
        var actual = personService.listAll();

        assertEquals(expectedPersonDTOList, actual);
    }

    @Test
    void testDeletePersonThenResultDelete() throws PersonNotFoundException {
        Person fakeEntity = createFakeEntity();
        when(personRepository.findById(fakeEntity.getId())).thenReturn(Optional.of(fakeEntity));

        personService.delete(fakeEntity.getId());

        verify(personRepository, times(1)).deleteById(fakeEntity.getId());
    }

    @Test
    void testGiveAnIdThenFindPerson() throws PersonNotFoundException {
        PersonDTO fakeDTO = createFakeDTO();
        Person fakeEntity = createFakeEntity();
        fakeDTO.setId(1L);
        fakeDTO.getPhones().get(0).setId(1L);
        when(personRepository.findById(fakeEntity.getId())).thenReturn(Optional.of(fakeEntity));

        PersonDTO actualPerson = personService.findById(fakeEntity.getId());

        assertEquals(fakeDTO, actualPerson);
    }

    @Test
    void testGiveIdAndPersonDTOThenUpdatePerson() throws PersonNotFoundException {
        PersonDTO fakeDTO = createFakeDTO();
        Person fakeEntity = createFakeEntity();
        fakeDTO.setId(1L);
        fakeDTO.getPhones().get(0).setId(1L);

        when(personRepository.findById(fakeEntity.getId())).thenReturn(Optional.of(fakeEntity));
        when(personRepository.save(fakeEntity)).thenReturn(fakeEntity);

        MessageResponseDTO expectedSuccessMessage = createExpectedMessageResponse(fakeEntity.getId(), "Updated person with ID");
        MessageResponseDTO succesMessage = personService.updateById(fakeDTO.getId(), fakeDTO);

        assertEquals(expectedSuccessMessage, succesMessage);
    }

    private MessageResponseDTO createExpectedMessageResponse(Long id, String modelMessage) {
        return MessageResponseDTO
                .builder()
                .message(modelMessage + " " + id)
                .build();
    }
}
