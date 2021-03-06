package com.feed_grabber.core.auth;

import com.feed_grabber.core.auth.dto.*;
import com.feed_grabber.core.auth.exceptions.InvitationExpiredException;
import com.feed_grabber.core.auth.exceptions.CorporateEmailException;
import com.feed_grabber.core.company.exceptions.CompanyAlreadyExistsException;
import com.feed_grabber.core.company.exceptions.CompanyNotFoundException;
import com.feed_grabber.core.company.exceptions.WrongCompanyNameException;
import com.feed_grabber.core.invitation.exceptions.InvitationNotFoundException;
import com.feed_grabber.core.apiContract.AppResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final RegisterService registerService;

    public AuthController(AuthService authService, RegisterService registerService) {
        this.authService = authService;
        this.registerService = registerService;
    }

    @ApiOperation(value = "Renovate the token", notes = "Provide a token to renovate it")
    @PostMapping("/renovate")
    @ResponseStatus(HttpStatus.CREATED)
    public AppResponse<TokenRefreshResponseDTO> renovate(@ApiParam(value = "Token to renovate",
            required = true) @RequestBody String token) {
        return new AppResponse<>(authService.refresh(token));
    }

    @ApiOperation(value = "Register new user", notes = "Provide an email, username, companyName and password to register")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AppResponse<RegisterUserResponseDto> register(@RequestBody UserRegisterDTO dto)
            throws WrongCompanyNameException, CompanyAlreadyExistsException {
        registerService.registerUser(dto);
        return new AppResponse<>(new RegisterUserResponseDto(true));
    }

    @ApiOperation(value = "Register new user by invitation", notes = "Provide an email, username, invitationId and password to register")
    @PostMapping("/invitation")
    @ResponseStatus(HttpStatus.CREATED)
    public AppResponse<AuthUserResponseDTO> registerByInvitation(@RequestBody UserRegisterInvitationDTO dto) throws InvitationNotFoundException, InvitationExpiredException {
        var pass = dto.getPassword();
        var companyId = registerService.registerUserByInvitation(dto);

        var loginDto = new UserLoginDTO(pass, dto.getUsername(), companyId);
        return login(loginDto);
    }

    @ApiOperation(value = "Register new user by corporate email",
            notes = "Provide an email, username, companyName and password to register")
    @PostMapping("/registerByEmail")
    @ResponseStatus(HttpStatus.CREATED)
    public AppResponse<RegisterUserResponseDto> registerByEmail(@RequestBody UserRegisterDTO dto)
            throws CompanyNotFoundException, CorporateEmailException {
        registerService.registerUserByEmail(dto);
        return new AppResponse<>(new RegisterUserResponseDto(true));
    }

    @ApiOperation(value = "Login", notes = "Provide a username and password to login")
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AppResponse<AuthUserResponseDTO> login(@RequestBody UserLoginDTO userLoginDTO) {
        var login = authService.login(userLoginDTO);
        return new AppResponse<>(login);

    }

}
