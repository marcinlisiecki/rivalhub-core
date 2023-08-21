package com.rivalhub;

import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationRepository;
import com.rivalhub.organization.controller.OrganizationUserController;
import com.rivalhub.organization.service.OrganizationSettingsService;
import com.rivalhub.organization.service.OrganizationUserService;
import com.rivalhub.organization.service.UserOrganizationService;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserRepository;
import org.apache.catalina.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class RivalHubApplication {
	public static void main(String[] args) {
		var context = SpringApplication.run(RivalHubApplication.class, args);
		PasswordEncoder passwordEncoder = context.getBean(PasswordEncoder.class);
		OrganizationRepository organizationRepository = context.getBean(OrganizationRepository.class);
		UserRepository userRepository = context.getBean(UserRepository.class);
		OrganizationUserService organizationUserService = context.getBean(OrganizationUserService.class);
		var organizationSettingsService = context.getBean(OrganizationSettingsService.class);

		Organization organization = new Organization();
		organization.setName("NCDC");
		organization.setId(1L);
		organization.setInvitationHash("123123123");

		UserData userAdmin = new UserData();
		userAdmin.setName("mati");
		userAdmin.setEmail("mateusz.szkudlarek55@gmail.com");
		userAdmin.setPassword(passwordEncoder.encode("test123!"));
		userAdmin.setProfilePictureUrl(null);
		userRepository.save(userAdmin);

		UserOrganizationService.addAdminUser(userAdmin, organization);


		UserData userNew= new UserData();
		userNew.setName("andre");
		userNew.setEmail("andrzej.kuzminski12@gmail.com");
		userNew.setPassword(passwordEncoder.encode("test123!"));
		userNew.setProfilePictureUrl(null);
		userRepository.save(userNew);
		UserOrganizationService.addAdminUser(userNew, organization);
//		UserOrganizationService.addUser(userNew, organization);

		for (long i = 1; i < 100; i++) {
			UserData user = new UserData();
			user.setName("User " + i);
			user.setEmail(i + "@gmail.com");
			user.setProfilePictureUrl(null);
			organization.getUserList().add(user);
			userRepository.save(user);
		}
		organizationRepository.save(organization);
	}
}
