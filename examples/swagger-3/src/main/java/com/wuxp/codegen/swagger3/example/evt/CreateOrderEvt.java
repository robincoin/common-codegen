package com.wuxp.codegen.swagger3.example.evt;


import com.wuxp.api.signature.ApiSignature;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.beans.Transient;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Data
public class CreateOrderEvt extends BaseEvt {

    private static DateFormat dateFormat = new SimpleDateFormat();

    @Size(max = 50)
    @ApiSignature
    private String sn;

    @NotNull
    private transient Integer totalAmount;

    @Transient
    public Integer getTotalAmount() {
        return totalAmount;
    }
}
