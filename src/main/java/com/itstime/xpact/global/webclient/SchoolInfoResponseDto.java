package com.itstime.xpact.global.webclient;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;

import java.util.List;

@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
public class SchoolInfoResponseDto {

    private Header header;
    private Body body;

    @Getter
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Header {
        @XmlElement(name = "resultCode")
        private String resultCode;

        @XmlElement(name = "resultMsg")
        private String resultMessage;

        public Header() {}
    }

    @Getter
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Body {
        private Items items;

        @XmlElement(name = "totalCount")
        private int totalCount;

        public Body() {}

    }

    @Getter
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Items {
        @XmlElement(name = "item")
        private List<Item> itemList;

        public Items() {}

    }

    @Getter
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Item {
        @XmlElement(name = "schlNm")
        private String schoolName;

        @XmlElement(name = "korMjrNm")
        private String major;

        public Item() {}
    }

    public SchoolInfoResponseDto() {}
}