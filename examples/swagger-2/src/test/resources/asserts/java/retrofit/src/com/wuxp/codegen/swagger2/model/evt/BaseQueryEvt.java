package com.wuxp.codegen.swagger2.model.evt;

import lombok.Data;
import javax.validation.constraints.*;
        import com.wuxp.codegen.swagger2.model.evt.BaseEvt;

    /**
        * 统一的查询对象
    **/
@Data
public class  BaseQueryEvt extends BaseEvt {

            /**
                *属性说明：查询大小，示例输入：
                *字段在java中的类型为：Integer
            **/
         Integer query_size;

            /**
                *属性说明：查询页码，示例输入：
                *字段在java中的类型为：Integer
            **/
         Integer query_page;

}
