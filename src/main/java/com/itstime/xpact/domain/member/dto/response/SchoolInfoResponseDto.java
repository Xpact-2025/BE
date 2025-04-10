package com.itstime.xpact.domain.member.dto.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.List;

@Data
@JacksonXmlRootElement(localName = "response")
public class SchoolInfoResponseDto {

    private Header header;
    private Body body;

    @Data
    public static class Header {

        @JacksonXmlProperty(localName = "resultCode")
        private String resultCode;
        @JacksonXmlProperty(localName = "resultMsg")
        private String resultMessage;
    }

    @Data
    public static class Body {
        private Items items;
        @JacksonXmlProperty(localName = "totalCount")
        private int totalCount;
    }

    @Data
    public static class Items {
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "item")
        private List<Item> itemList;
    }

    @Data
    public static class Item {
        @JacksonXmlProperty(localName = "schlNm")
        private String schoolName;
        @JacksonXmlProperty(localName = "korMjrNm")
        private String major;
    }
}
