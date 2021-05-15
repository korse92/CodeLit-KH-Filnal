package com.kh.codelit.admin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.codelit.admin.model.service.AdminService;
import com.kh.codelit.common.HelloSpringUtils;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/admin")
@Slf4j
public class adminController {

	@Autowired
	private AdminService adminService;

	@GetMapping("/manageMemberIndex.do")
	public void manageMemberIndex() {
	}

	// 강의 신청 목록
	@GetMapping("/applyLectureList.do")
	public ModelAndView applyLectureList(ModelAndView mav) {

		try {
			List<Map<String, Object>> list = adminService.applyLectureList();
			log.debug("list={}", list);

			mav.addObject("list", list);
			mav.setViewName("/admin/applyLectureList");

		} catch (Exception e) {
			// 1 로깅 작업
			log.error(e.getMessage(), e);
			// 2 다시 spring container에 던져줄것
			throw e;
		}
		return mav;
	}

	// 모달에서 강의 승인
	@GetMapping("/approveLecture.do")
	public void approveLecture(@RequestParam String id, Model model) {

		model.addAttribute("id", id);

	}

	// 모달에서 보낸 강의승인폼
	@PostMapping("/approveLecture.do")
	public String approveLecture_(@RequestParam String id, RedirectAttributes redirectAttr) {

		String msg = null;
		try {
			int result = adminService.approveLecture(id);
			msg = "강의 승인에 성공하였습니다";

		} catch (Exception e) {
			msg = "승인에 실패하였습니다";
			throw e;
		}
		redirectAttr.addAttribute("msg", msg);

		return "redirect:/admin/approveLecture.do";

	}

	/**
	 * 강사 신청목록페이지 섹션
	 */

	// 강사신청 목록 출력
	@GetMapping("/applyTeacherList.do")
	public ModelAndView applyTeacherList(ModelAndView mav) {
		try {
			List<Map<String, Object>> list = adminService.applyTeacherList();
			log.debug("list ={}", list);

			mav.addObject("list", list);
			mav.setViewName("/admin/applyTeacherList");

		} catch (Exception e) {
			// 1 로깅 작업
			log.error(e.getMessage(), e);
			// 2 다시 spring container에 던져줄것
			throw e;
		}
		return mav;
	}

	@GetMapping("/approveTeacher.do")
	public void approveTeacher(@RequestParam String id, Model model) {

		model.addAttribute("id", id);

	}

	// 모달에서 보낸 강사 승인폼
	@PostMapping("/approveTeacher.do")
	public String approveTeacher_(@RequestParam String id, RedirectAttributes redirectAttr) {

		String msg = null;
		try {
			int result = adminService.approveTeacher(id);
			msg = "승인에 성공하였습니다";

		} catch (Exception e) {
			msg = "승인에 실패하였습니다";
			throw e;
		}
		redirectAttr.addFlashAttribute("msg", msg);

		return "redirect:/admin/applyTeacherList.do";

	}

	// 강사 승인 취소 -> 삭제처리
	@PostMapping("/deleteTeacher.do")
	public String deleteTeacher(@RequestParam String id, RedirectAttributes redirectAttr) {
		String msg = null;
		log.debug("id = {}", id);

		try {

			int result = adminService.deleteTeacher(id);
			msg = "강사 신청 거절에 성공하였습니다.";
			log.debug("result={}", result);
		} catch (Exception e) {
			msg = "강사 신청 거절에 실패하였습니다";
			throw e;
		}
		redirectAttr.addFlashAttribute("msg", msg); // session에 담았다가 다음 redirect떄 불러오는것 | addAttribute : redirect시 경로를
													// /spring?msg=수정성공 이런식으로 넘어감
		return "redirect:/admin/applyTeacherList.do";
	}

	// 강의 신청 목록에서 삭제처리
	@PostMapping("/deleteLecture")
	public String deleteLecture(@RequestParam String id, RedirectAttributes redirectAttr) {
		String msg = null;
		try {

			int result = adminService.deleteLecture(id);
			msg = "강의 신청 거절에 성공하였습니다.";

		} catch (Exception e) {
			msg = "강사 신청 거절에 실패하였습니다";
			throw e;
		}

		redirectAttr.addFlashAttribute("msg", msg);
		return "redirect:/admin/applyLectureList.do";
	}

	@GetMapping("/manageLectureBoard.do")
	public void manageLectureBoard(@RequestParam(defaultValue = "1") int cPage, Model model,
			HttpServletRequest request) {

		// 1 .사용자 입력값
		int numPerPage = 5;
		// log.debug("cPage= {}", cPage);
		Map<String, Object> param = new HashMap<>();
		param.put("numPerPage", numPerPage);
		param.put("cPage", cPage);

		// 2. 업무로직
		// a. contents 영역 -> mybatis의 rowBounds 사용
		List<Map<String, Object>> list = adminService.selectAllLecture(param);
		log.debug("list = {}", list);

		// b. pageBar 영역
		int totalContents = adminService.getTotalContents();
		// log.debug("totalContents = {}", totalContents);
		String url = request.getRequestURI();
		// log.debug("url = {}",url);
		String pageBar = HelloSpringUtils.getPageBar(totalContents, cPage, numPerPage, url);
		log.debug("pageBar = {}", pageBar);
		// 3. jsp처리 위임 model.addAttribute("list",list);
		model.addAttribute("list", list);
		model.addAttribute("pageBar", pageBar);

	}

	@GetMapping("/rejectTeacherRight.do")
	public void rejectTeacherRight() {

	}

	@PostMapping("/rejectTeacherRight.do")
	public void rejectTeacherRight_() {
		log.debug("강사 권한 해지 {}", "도착");
	}

	// 검색창
	@GetMapping("/searchKeyword.do")
	@ResponseBody
	public List<Map<String, Object>> searchKeyword(@RequestParam String searchKeyword) {

		log.debug("searchKeyword = {}", searchKeyword);
		List<Map<String, Object>> categoryList = adminService.selectAllBySearching(searchKeyword);
		log.debug("categoryList = {}", categoryList);

		return categoryList;

	}
	
	//@GetMapping("/searchCategory.do/{}")

}
