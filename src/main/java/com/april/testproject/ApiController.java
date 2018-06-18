package com.april.testproject;

import com.april.testproject.config.AppUserDetailsService;
import com.april.testproject.dto.IdeaDto;
import com.april.testproject.dto.UserDto;
import com.april.testproject.entity.Idea;
import com.april.testproject.entity.Tag;
import com.april.testproject.entity.User;
import com.april.testproject.entity.UserRoleEnum;
import com.april.testproject.repository.IdeaRepository;
import com.april.testproject.repository.TagRepository;
import com.april.testproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.*;

import static com.april.testproject.utils.ApiUtils.encryptPassword;

@Component
@RestController
@RequestMapping("/api/v1")
public class ApiController {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private IdeaRepository ideaRepository;

	@Autowired
	private TagRepository tagRepository;

//@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping(value = "registration", consumes = "application/json")
	public Object createUser(@Valid @RequestBody UserDto userDto) {
		User user = new User();
		user.setFirstName(userDto.getFirstName());
		user.setEmail(userDto.getEmail());
		user.setRole(UserRoleEnum.ROLE_USER.toString());
		user.setCountry(userDto.getCountry());
		user.setPassword(encryptPassword(userDto.getPassword()));
		user.setTags(userDto.getTags());
		user.setLastName(userDto.getLastName());
		user.setAvatarPicture(userDto.getAvatarPicture());
		user.setAboutUser(userDto.getAboutUser());
		user.setAboutCompany(userDto.getAboutCompany());
		user.setCountry(userDto.getCountry());
		user.setCity(userDto.getCity());
		user.setRegDate(new Date());
		userRepository.save(user);
		return user;
	}

	@GetMapping(value = "login", consumes = "application/json")
	public Object login() {
		User user = AppUserDetailsService.getUser();
		return user;
	}

	@PostMapping(value = "idea", consumes = "application/json")
	public Object createIdea(@RequestBody IdeaDto ideaDto) {
		Idea idea = new Idea();
		idea.setStatus(ideaDto.getStatus());
		idea.setUserId(ideaDto.getUserId());
		idea.setHeader(ideaDto.getHeader());
		idea.setMainPicture(ideaDto.getMainPicture());
		idea.setShortDescription(ideaDto.getShortDescription());
		idea.setFullDescription(ideaDto.getFullDescription());
		idea.setPictureList(ideaDto.getPictureList());
		idea.setRate(0);
		idea.setCreationDate(new Date());
		idea.setPrice(ideaDto.getPrice());
		idea.setWhoLiked("");
		Set<Tag> tags = getTags(ideaDto.getTags());
		idea.setTags(tags);
		ideaRepository.save(idea).getId();
		return idea;
	}

	@GetMapping(value = "user/{id}", consumes = "application/json")
	public Object getUserById(@PathVariable(value = "id") Long userId) {
		return userRepository.findOne(userId);
	}

	@GetMapping(value = "users", consumes = "application/json")
	public Object getUsers() {
		return userRepository.findAll();
	}

	@GetMapping(value = "tags", consumes = "application/json")
	public Object getTags() {
		return tagRepository.findAll();
	}

	@GetMapping(value = "idea/{id}", consumes = "application/json")
	public Object getIdeaById(@PathVariable(value = "id") Long ideaId) {
		return ideaRepository.findOne(ideaId);
	}

	@GetMapping(value = "tag/{id}", consumes = "application/json")
	public Object getTagById(@PathVariable(value = "id") Long id) {
		return tagRepository.findOne(id);
	}

	@GetMapping(value = "ideas", consumes = "application/json")
	public Object getIdeas() {
		return ideaRepository.findAll();
	}

	@GetMapping(value = "getIdeasByUserId/{userId}", consumes = "application/json")
	public List<Idea> getIdeasByUserId(@PathVariable("userId") String userId) {
		return ideaRepository.findByUserId(userId);
	}

	@GetMapping(value = "getIdeasByHeader/{word}", consumes = "application/json")
	public List<Idea> getIdeasByHeader(@PathVariable("word") String word) {
		return ideaRepository.findInHeader(word);
	}

	@GetMapping(value = "getTagsByIdeaId/{ideaId}", consumes = "application/json")
	public List<String> getTagsByIdeaId(@PathVariable("ideaId") String ideaId) {
		return ideaRepository.getOne(Long.valueOf(ideaId)).getTags();
	}

	@PutMapping(value = "user", consumes = "application/json")
	public Object updateUser(@Valid @RequestBody UserDto userDto) {
		Long id = userDto.getId();
		User user = userRepository.findOne(id);
		if (userDto.getCountry() != null) user.setCountry(userDto.getCountry());
		if (userDto.getEmail() != null) user.setEmail(userDto.getEmail());
		if (userDto.getFirstName() != null) user.setFirstName(userDto.getFirstName());
		if (userDto.getRole() != null) user.setRole(userDto.getRole());
		userRepository.save(user);
		return user;
	}

	@PutMapping(value = "idea", consumes = "application/json")
	public Object updateIdea(@Valid @RequestBody IdeaDto ideaDto) {
		Long id = ideaDto.getId();
		Idea idea = ideaRepository.findOne(id);
		if (ideaDto.getStatus() != null) idea.setStatus(ideaDto.getStatus());
		if (ideaDto.getUserId() != null) idea.setUserId(ideaDto.getUserId());
		if (ideaDto.getHeader() != null) idea.setHeader(ideaDto.getHeader());
		if (ideaDto.getMainPicture() != null) idea.setMainPicture(ideaDto.getMainPicture());
		if (ideaDto.getShortDescription() != null) idea.setShortDescription(ideaDto.getShortDescription());
		if (ideaDto.getFullDescription() != null) idea.setFullDescription(ideaDto.getFullDescription());
		if (ideaDto.getPictureList() != null) idea.setPictureList(ideaDto.getPictureList());
		if (ideaDto.getPrice() != null) idea.setPrice(ideaDto.getPrice());
		if (ideaDto.getWhoLiked() != null) idea.setWhoLiked(ideaDto.getWhoLiked());
		if (ideaDto.getTags() != null) {
			Set<Tag> tags = getTags(ideaDto.getTags());
			idea.setTags(tags);
		}
		ideaRepository.save(idea);
		return idea;
	}

	@DeleteMapping(value = "user/{id}")
	public Object deleteUser(@PathVariable("id") Long id) {
		userRepository.delete(id);
		return id;
	}

	@DeleteMapping(value = "idea/{id}")
	public Object deleteIdea(@PathVariable("id") Long id) {
		ideaRepository.delete(id);
		return id;
	}

	@GetMapping(value = "getUserByName/{name}")
	public List<User> getUserByName(@PathVariable("name") String name) {
		return userRepository.findByFirstNameContaining(name);
	}

	@GetMapping(value = "getUserByEmail/{email}")
	public User getUserByEmail(@PathVariable("email") String email) {
		return userRepository.findByEmailContaining(email).get(0);
	}

	private Set<Tag> getTags(String tagString) {
		if (tagString == null) return null;
		List strings = Arrays.asList(tagString.split(","));
		Set<String> tagNameSet = new HashSet<>();
		if (strings.size() != 0) tagNameSet.addAll(strings);
		Set<Tag> tags = new HashSet<>();
		for (String tagName : tagNameSet){
			tags.add(tagRepository.findByTagName(tagName).get(0));
		}
		return tags;
	}

}
