
import {Enum} from "oak_weex_common/src/enums/Enum";

export default class Sex{

   constructor() {}

    public static readonly MAN:Enum={
      name:"MAN",
      ordinal:0,
      desc: "男"
    };
    public static readonly WOMAN:Enum={
      name:"WOMAN",
      ordinal:1,
      desc: "女"
    };
    public static readonly NONE:Enum={
      name:"NONE",
      ordinal:2,
      desc: "未知"
    };


}