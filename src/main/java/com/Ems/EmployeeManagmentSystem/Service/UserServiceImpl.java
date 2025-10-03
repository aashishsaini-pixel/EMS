package com.Ems.EmployeeManagmentSystem.Service;

import com.Ems.EmployeeManagmentSystem.DTO.Request.LoginRequestDTO;
import com.Ems.EmployeeManagmentSystem.DTO.Request.UserRequestDTO;
import com.Ems.EmployeeManagmentSystem.DTO.Response.JwtResponse;
import com.Ems.EmployeeManagmentSystem.DTO.Response.UserResponseDTO;
import com.Ems.EmployeeManagmentSystem.Entity.Users;
import com.Ems.EmployeeManagmentSystem.Exceptions.AuthenticationFailedException;
import com.Ems.EmployeeManagmentSystem.Exceptions.UserAlreadyExistsException;
import com.Ems.EmployeeManagmentSystem.Exceptions.UserNotFoundException;
import com.Ems.EmployeeManagmentSystem.Mapper.UserMapper;
import com.Ems.EmployeeManagmentSystem.Repository.UsersRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UsersRepository usersRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private String tokenType = "Bearer";

    @Value("${employee.import.batch-size}")
    private int BATCH_SIZE;

    @Override
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        Optional<Users> existingUserOpt = usersRepository.findByEmail(userRequestDTO.getEmail());

        if (existingUserOpt.isPresent()) {
            Users existingUser = existingUserOpt.get();

            if (existingUser.getRole().toString().equals(userRequestDTO.getRole())){
                log.info("User already exists with email {} and role {}", userRequestDTO.getEmail(), userRequestDTO.getRole());
                throw new UserAlreadyExistsException("User already exists with email " + userRequestDTO.getEmail() + " and role " + userRequestDTO.getRole());
            }

            log.info("User already exists with email {}", userRequestDTO.getEmail());
            throw new UserAlreadyExistsException("User already exists with email " + userRequestDTO.getEmail());
        }

        Users newUser = userMapper.toEntity(userRequestDTO);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.setIsActive(true);
        newUser.setIsDeleted(false);
        Users savedUser = usersRepository.save(newUser);

        return userMapper.toDto(savedUser);
    }

    @Override
    public JwtResponse login(LoginRequestDTO loginRequestDTO) {
        log.info("Login user with email : {}", loginRequestDTO.getEmail());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDTO.getEmail(),
                            loginRequestDTO.getPassword()
                    )
            );

            if (authentication.isAuthenticated()) {
                log.info("User logged in with email : {}", loginRequestDTO.getEmail());
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                String token = jwtService.generateToken(userDetails);

                return new JwtResponse(token ,tokenType , userDetails.getUsername() );
            } else {
                log.warn("Authentication failed , cannot authenticate the user with email {}", loginRequestDTO.getEmail());
                throw new BadCredentialsException("Invalid credentials");
            }

        } catch (AuthenticationException e){
            throw new AuthenticationFailedException("Invalid credentials : user not found with email : " + loginRequestDTO.getEmail() );
        }
    }

    @Transactional
    public void exportUsersPaginated(HttpServletResponse response) throws IOException {
        log.info("Starting CSV export for all users");
        long startTime = System.currentTimeMillis();

        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = String.format("users_export_%s.csv", timestamp);

            response.setContentType("text/csv; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

            Writer writer = new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8);

            // Add BOM for Excel compatibility
            writer.write('\ufeff');

            // CSV Header
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader("User ID", "Email", "Role", "Is Active", "Is Deleted", "Created At", "Updated At")
                    .setDelimiter(',')
                    .setRecordSeparator("\r\n")
                    .setQuoteMode(org.apache.commons.csv.QuoteMode.MINIMAL)
                    .build();

            try (CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)) {

                int pageNumber = 0;
                int batchSize = BATCH_SIZE;
                int totalExported = 0;
                Page<Users> page;

                do {
                    Pageable pageable = PageRequest.of(pageNumber, batchSize, Sort.by(Sort.Direction.ASC, "id"));
                    page = usersRepository.findAll(pageable);

                    log.debug("Processing batch {}: {} users", pageNumber + 1, page.getNumberOfElements());

                    for (Users user : page.getContent()) {
                        csvPrinter.printRecord(
                                user.getId() != null ? user.getId() : "",
                                user.getEmail() != null ? user.getEmail() : "",
                                user.getRole() != null ? user.getRole().name() : "",
                                user.getIsActive() != null ? user.getIsActive().toString() : "false",
                                user.getIsDeleted() != null ? user.getIsDeleted().toString() : "false",
                                user.getCreatedAt() != null ? user.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "",
                                user.getUpdatedAt() != null ? user.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : ""
                        );
                        totalExported++;
                    }

                    pageNumber++;

                } while (page.hasNext());

                csvPrinter.flush();

                long duration = System.currentTimeMillis() - startTime;
                log.info("User CSV export completed. Total users: {}, Duration: {}ms", totalExported, duration);
            }

        } catch (IOException e) {
            log.error("IO error during user CSV export", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during user CSV export", e);
            throw new RuntimeException("Failed to export users to CSV", e);
        }
    }

    @Override
    public UserResponseDTO deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);

        Users user = usersRepository.findById(id).orElseThrow(() -> {
            log.warn("User not found with ID: {}", id);
            return new UserNotFoundException("User not found with ID: " + id);
        });

        try {
            user.setIsDeleted(true);
            user.setIsActive(false);

            if (user.getEmployee() != null) {
                user.getEmployee().setIsDeleted(true);
                user.getEmployee().setIsActive(false);
            }

            Users updatedUser = usersRepository.save(user);
            log.info("User with ID {} marked as deleted successfully.", id);
            return userMapper.toDto(updatedUser);
        } catch (Exception e) {
            log.error("Failed to delete user with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete user with ID: " + id, e);
        }
    }

}
