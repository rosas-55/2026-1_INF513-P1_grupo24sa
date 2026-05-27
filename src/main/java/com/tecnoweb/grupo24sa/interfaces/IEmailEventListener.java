package com.tecnoweb.grupo24sa.interfaces;

import com.tecnoweb.grupo24sa.utils.Email;

import java.util.List;

public interface IEmailEventListener {
    void onReceiveEmailEvent(List<Email> emails);
}
