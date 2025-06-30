package com.tech.prjm09.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class BDto {
	private int bid;
	private String bname;
	private String btitle;
	private String bcontent;
	private java.sql.Timestamp bdate;
	private int bhit;
	private int bgroup;
	private int bstep;
	private int bindent;

}
