package com.tech.prjm09.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.tech.command.BCommand;
import com.tech.command.BContentCommand;
import com.tech.command.BDeleteCommand;
import com.tech.command.BListCommand;
import com.tech.command.BModifyCommand;
import com.tech.command.BModifyViewCommand;
import com.tech.command.BReplyCommand;
import com.tech.command.BReplyViewCommand;
import com.tech.command.BWriteCommand;
import com.tech.prjm09.dao.IDao;
import com.tech.prjm09.dto.BDto;
import com.tech.prjm09.utill.SearchVO;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class BController {
	BCommand command;
	
	private final IDao iDao;
	
	@Autowired
	public BController(IDao iDao) {
		this.iDao=iDao;
	}
	
	@RequestMapping("/")
	private String index() {
		return "index";
	}
	
	@RequestMapping("/list")
	private String list(HttpServletRequest request,Model model,SearchVO searchVO) {
		System.out.println("list()");
//		command=new BListCommand();
//		command.execute(model);
		
//		searching
		String btitle="";
		String bcontent="";
		
		String[] brdTitle=request.getParameterValues("searchType");
		if (brdTitle!=null) {
		for (int i = 0; i < brdTitle.length; i++) {
			System.out.println("brdTitle :"+brdTitle[i]);
		}	
	}
		
		if (brdTitle!=null) {
		for (String val : brdTitle) {
			if (val.equals("btitle")) {
				model.addAttribute("btitle","true");
				btitle="btitle";
			}if (val.equals("bcontent")) {
				model.addAttribute("bcontent","true");
				bcontent="bcontent";
			}
		}	
	}
		String searchKeyword=request.getParameter("sk");
		if (searchKeyword==null) 
			searchKeyword="";
		model.addAttribute("searchKeyword",searchKeyword);
		
//		-------------------------------------------
//		전체글의 갯수 변형
		int total=0;
		
		if (btitle.equals("btitle") && bcontent.equals("")) {
			total=iDao.selectBoardCount(searchKeyword,"1");
			System.out.println("total11111111111111");
		}else if (btitle.equals("") && bcontent.equals("bcontent")) {
			total=iDao.selectBoardCount(searchKeyword,"2");
			System.out.println("total222222222222222222");
		}else if (btitle.equals("btitle") && bcontent.equals("bcontent")) {
			total=iDao.selectBoardCount(searchKeyword,"3");
			System.out.println("total3333333333");
		}else if (btitle.equals("") && bcontent.equals("")) {
			total=iDao.selectBoardCount(searchKeyword,"4");
			System.out.println("total4444444444444444");
		}
		
		//글의 총갯수
//		int total=iDao.selectBoardCount();
		System.out.println("total : "+total);
		searchVO.pageCalculate(total);
		
//		paging
		String strPage=request.getParameter("page");
		//null검사
		if (strPage==null) {
			strPage="1";
		}
		int page=Integer.parseInt(strPage);
		searchVO.setPage(page);	
		
		System.out.println("total :"+total);
		System.out.println("click page :"+strPage);
		System.out.println("pageStart :"+searchVO.getPageStart());
		System.out.println("pageEnd : "+searchVO.getPageEnd());
		System.out.println("pageTotal :"+searchVO.getTotPage());
		System.out.println("rowStart : "+searchVO.getRowStart());
		System.out.println("rowEnd : "+searchVO.getRowEnd());
		
		int rowStart=searchVO.getRowStart();
		int rowEnd=searchVO.getRowEnd();
		
//		ArrayList<BDto> list=null;
		if (btitle.equals("btitle") && bcontent.equals("")) {
//			total=iDao.selectBoardCount(searchKeyword,"1");
			model.addAttribute("list",iDao.list(rowStart,rowEnd,searchKeyword,"1"));
//			list=iDao.list(rowStart,rowEnd,searchKeyword,"1");
			System.out.println("total11111111111111");
		}else if (btitle.equals("") && bcontent.equals("bcontent")) {
			model.addAttribute("list",iDao.list(rowStart,rowEnd,searchKeyword,"2"));
//			list=iDao.list(rowStart,rowEnd,searchKeyword,"2");
			System.out.println("total222222222222222222");
		}else if (btitle.equals("btitle") && bcontent.equals("bcontent")) {
			model.addAttribute("list",iDao.list(rowStart,rowEnd,searchKeyword,"3"));
//			list=iDao.list(rowStart,rowEnd,searchKeyword,"3");
			System.out.println("total3333333333");
		}else if (btitle.equals("") && bcontent.equals("")) {
			model.addAttribute("list",iDao.list(rowStart,rowEnd,searchKeyword,"4"));
//			list=iDao.list(rowStart,rowEnd,searchKeyword,"4");
			System.out.println("total4444444444444444");
		}
		
//		model.addAttribute("list",list);
		
		model.addAttribute("totRowCnt",total);
		model.addAttribute("searchVo",searchVO);
		
		return "list";

	}
	
	@RequestMapping("/write_view")
	private String write_view(Model model) {
		
		return "write_view";
	}
	
	@RequestMapping("/write")
	private String write(MultipartHttpServletRequest mtrequest, Model model) {

		String bname=mtrequest.getParameter("bname");
		String btitle=mtrequest.getParameter("btitle");
		String bcontent=mtrequest.getParameter("bcontent");
		iDao.write(bname, btitle, bcontent);
		
		String workPath=System.getProperty("user.dir");
		String root=workPath+"\\src\\main\\resources\\static\\files";
		
		List<MultipartFile> fileList=mtrequest.getFiles("file");
		
		int bid=iDao.selBid();
		
		for (MultipartFile mf : fileList) {
			String originalFile=mf.getOriginalFilename();
			System.out.println("originalfiles :"+originalFile);
			long longtime=System.currentTimeMillis();
			String changeFile=longtime+"_"+originalFile;
			System.out.println("change Files :"+changeFile);
			
			String pathfile=root+"\\"+changeFile;
			try {
				if (!originalFile.equals("")) {
					mf.transferTo(new File(pathfile));
					iDao.imgwrite(bid,originalFile,changeFile);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return "redirect:list";
	}
	
	@RequestMapping("/content_view")
	private String content_view(HttpServletRequest request,Model model) {
//		model.addAttribute("request", request);
//		command=new BContentCommand();
//		command.execute(model);
		String bid=request.getParameter("bid");
		BDto dto=iDao.contentView(bid);
		model.addAttribute("content_view",dto);
		
		return "content_view";
	}
	
	@RequestMapping("/modify_view")
	private String modify_view(HttpServletRequest request,Model model) {
//		model.addAttribute("request", request);
//		command=new BModifyViewCommand();
//		command.execute(model);
		String bid=request.getParameter("bid");
		BDto dto=iDao.modifyView(bid);
		model.addAttribute("content_view",dto);
		
		return "modify_view";
	}
	
	@RequestMapping("/modify")
	private String modify(HttpServletRequest request,Model model) {
//		model.addAttribute("request", request);
//		command=new BModifyCommand();
//		command.execute(model);
		String bid=request.getParameter("bid");
		String bname=request.getParameter("bname");
		String btitle=request.getParameter("btitle");
		String bcontent=request.getParameter("bcontent");
		iDao.modify(bid, bname, btitle, bcontent);
		
		return "redirect:list";
	}
	
	@RequestMapping("/reply_view")
	private String reply_view(HttpServletRequest request,Model model) {
//		model.addAttribute("request", request);
//		command=new BReplyViewCommand();
//		command.execute(model);
		String bid=request.getParameter("bid");
		BDto dto=iDao.reply_View(bid);
		model.addAttribute("reply_view",dto);
		
		return "reply_view";
	}

	@RequestMapping("/reply")
	private String reply(HttpServletRequest request,Model model) {
//		model.addAttribute("request", request);
//		command=new BReplyCommand();
//		command.execute(model);
		String bid=request.getParameter("bid");
		String bname=request.getParameter("bname");
		String btitle=request.getParameter("btitle");
		String bcontent=request.getParameter("bcontent");
		String bgroup=request.getParameter("bgroup");
		String bstep=request.getParameter("bstep");
		String bindent=request.getParameter("bindent");
		iDao.replyShape(bgroup, bstep);
		iDao.reply(bid, bname, btitle, bcontent, bgroup, bstep, bindent);
		
		return "redirect:list";
	}
	
	@RequestMapping("/delete")
	private String delete(HttpServletRequest request,Model model) {
//		model.addAttribute("request", request);
//		command=new BDeleteCommand();
//		command.execute(model);
		String bid=request.getParameter("bid");		
		iDao.delete(bid);
		
		return "redirect:list";
	}
	
	
}
