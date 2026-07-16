package com.riya.dentalbot.service;

import com.riya.dentalbot.dto.request.RegisterClinicRequest;
import com.riya.dentalbot.dto.response.RegisterClinicResponse;

public interface ClinicService {

    RegisterClinicResponse registerClinic(RegisterClinicRequest request);

}