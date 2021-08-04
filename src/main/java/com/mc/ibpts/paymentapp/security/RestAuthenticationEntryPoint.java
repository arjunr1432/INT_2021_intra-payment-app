package com.mc.ibpts.paymentapp.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mc.ibpts.paymentapp.common.gen.model.ErrorResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {


	@Override
	public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
		ErrorResponse errorResponse = new ErrorResponse().code(401).message("Request Unauthorised, validate the credentials.");
		httpServletResponse.setHeader("Content-Type", "application/json");
		httpServletResponse.setStatus(401);

		OutputStream out = httpServletResponse.getOutputStream();
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(out, errorResponse);
		out.flush();
	}
}