package com.fei.mcresweb.restservice.info;

import lombok.Getter;

public class InitInfo extends BasicInfo {
    @Getter
    public final boolean init = true;

    InitInfo(String catalogue, String essay, String user, String day, String visit) {
        super(catalogue, essay, user, day, visit);
    }
}
