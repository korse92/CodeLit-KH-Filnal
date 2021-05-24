package com.kh.codelit.teacher.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.kh.codelit.attachment.model.exception.AttachmentException;
import com.kh.codelit.attachment.model.vo.Attachment;
import com.kh.codelit.common.HelloSpringUtils;
import com.kh.codelit.lecture.model.service.LectureService;
import com.kh.codelit.lecture.model.vo.Lecture;
import com.kh.codelit.member.model.service.MemberService;
import com.kh.codelit.member.model.vo.Member;
import com.kh.codelit.teacher.model.service.TeacherService;
import com.kh.codelit.teacher.model.vo.Teacher;

import lombok.extern.slf4j.Slf4j;



@Slf4j
@Controller
@RequestMapping("/teacher")
public class TeacherController {

	@Autowired
	private TeacherService teacherService;

	@Autowired
	private LectureService lectureService;

	@Autowired
	private MemberService memberService;



	@GetMapping("/teacherRequest.do")
	public ModelAndView teacherRequest(
				Authentication authentication,
				ModelAndView mav
			) {
		log.debug("강사등록 요청 {}", "컨트롤러 매핑 도착");

		try {
			Member loginMember = (Member)authentication.getPrincipal();
			log.debug("loginMember = {}", loginMember);
			mav.addObject("loginMember", loginMember);

			List<Map<String, Object>> list = lectureService.selectCategoryListInstance();
			mav.addObject("catList", list);

			mav.setViewName("teacher/teacherRequest");
		} catch(Exception e) {
			throw e;
		}

		return mav;
	}


	@PostMapping("/teacherRequest.do")
	public ModelAndView teacherRequest(
				@ModelAttribute Teacher teacher,
				@RequestParam(value="upFile", required = false) MultipartFile upFile,
				ModelAndView mav,
				HttpServletRequest request,
				Authentication authentication
			) {

		int result = 0;
		String msg = null;
		log.debug("{}", "강사신청 포스트 도착");


		try {

			// 0. 파일 저장
			// 저장경로
			// pageContext - request - session - application에서 app이 servletContext이다.
			// 가져온 경로의 앞 /는 src/main/webapp을 가리킴. (컨텍스트 루트)
			String saveDirectory = request.getServletContext().getRealPath("/resources/upload/member");

			// File은 오로지 파일만 가리키는 것이 아니라, 존재하지 않는 것도 가리킬 수 있음. (생성용)
			File dir = new File(saveDirectory);
			if(!dir.exists()) {
				dir.mkdirs();	// 복수개 폴더 생성 가능 (경로상에 없는거 다 만들어줌)
			}



			if(upFile.isEmpty()) {

				result = teacherService.insertTeacherRequest(teacher);

			} else {

				// 디비를 가져와야 기존 파일 삭제가 필요한지 알 수 있다.
				Member member = memberService.selectOneMember(teacher.getRefMemberId());
				String oldFilePath = request.getServletContext().getRealPath("/resources/upload/member/" + member.getMemberReProfile());
				log.debug("기존파일경로 = {}", oldFilePath);
				File oldFile = new File(oldFilePath);

				// 리네임드파일 생성
				File renamedFile = HelloSpringUtils.getRenamedFile(saveDirectory, upFile.getOriginalFilename());

				log.debug("upFile = {}", upFile);
				log.debug("renamedFile = {}", renamedFile);


				// 오리지널네임과 리네임 담은 맵객체 생성
				Map<String, String> map = new HashMap<>();
				map.put("memberProfile", upFile.getOriginalFilename());
				map.put("memberReProfile", renamedFile.getName());
				map.put("id", teacher.getRefMemberId());

				// 티처 정보 및 파일네임을 담은 메소드
				result = teacherService.insertTeacherRequest(teacher, map);

				if(oldFile != null) oldFile.delete(); // 기존 파일 삭제
				upFile.transferTo(renamedFile);	// 업로드한 파일데이터를 지정한 파일에 저장한다.



				// authentication에 담긴 멤버 정보 변경
				member.setMemberProfile(upFile.getOriginalFilename());
				member.setMemberReProfile(renamedFile.getName());

				Authentication newAuthentication =
						new UsernamePasswordAuthenticationToken(
									member,
									authentication.getCredentials(),
									authentication.getAuthorities()
								);
				SecurityContextHolder.getContext().setAuthentication(newAuthentication);

				log.debug("강사신청 후 변경된 프로필 확인 = {}", ((Member)authentication.getPrincipal()).getMemberProfile());

			} // upfile.isEmpty()

			msg = "신청되었습니다.";

		} catch(IOException | IllegalStateException e) {
			log.error("강사 신청 첨부파일 오류", e);
			throw new RuntimeException("강사신청 첨부파일 저장 오류");
		} catch(Exception e) {
			msg = "신청에 실패했습니다.";
			throw e;
		}

		FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
		flashMap.put("msg", msg);

		mav.setViewName("redirect:/");

		return mav;
	}



	@GetMapping("/lectureEnroll.do")
	public void lectureEnroll() {
	}


	@PostMapping("/lectureEnroll.do")
	public String lectureEnroll(
			@ModelAttribute Lecture lecture,
			@RequestParam(required = false) MultipartFile lectureThumbnail,
			@RequestParam(value = "lectureHandout", required = false) MultipartFile[] lectureHandouts,
			HttpServletRequest request,
			Authentication authentication,
			RedirectAttributes redirectAttr) {

		try {
			log.debug("lecture(필드값 Set 전) = {}", lecture);

			//0.파일 저장 및 Attachment객체 생성/썸네일 Filename Set
			String thumbnailsSaveDirectory =
					request.getServletContext().getRealPath(Attachment.PATH_LECTURE_THUMBNAIL);
			String handoutsSaveDirectory =
					request.getServletContext().getRealPath(Attachment.PATH_LECTURE_HANDOUT);

			File dirThumb = new File(thumbnailsSaveDirectory);
			if(!dirThumb.exists()) {
				dirThumb.mkdirs(); // 복수개 폴더 생성 가능 (경로상에 없는거 다 만들어줌)
			}

			File dirHandout = new File(handoutsSaveDirectory);
			if(!dirHandout.exists()) {
				dirHandout.mkdirs(); // 복수개 폴더 생성 가능 (경로상에 없는거 다 만들어줌)
			}

			if(!lectureThumbnail.isEmpty() || !(lectureThumbnail.getSize() == 0)) {
				log.debug("lectureThumbnail = {}", lectureThumbnail);
				log.debug("lectureThumbnail.name = {}", lectureThumbnail.getOriginalFilename());
				log.debug("lectureThumbnail.size = {}", lectureThumbnail.getSize());

				//저장할 파일명 생성
				File renamedFile = HelloSpringUtils.getRenamedFile(thumbnailsSaveDirectory, lectureThumbnail.getOriginalFilename());
				//파일 저장
				lectureThumbnail.transferTo(renamedFile);

				lecture.setLectureThumbOrigin(lectureThumbnail.getOriginalFilename());
				lecture.setLectureThumbRenamed(renamedFile.getName());
			}

			List<Attachment> attachList = new ArrayList<>();

			for(MultipartFile lectureHandout : lectureHandouts) {
				if(lectureHandout.isEmpty() || lectureHandout.getSize() == 0)
					continue;

				log.debug("lectureHandout = {}", lectureHandouts);
				log.debug("lectureHandout.name = {}", lectureHandout.getOriginalFilename());
				log.debug("lectureHandout.size = {}", lectureHandout.getSize());

				//저장할 파일명 생성
				File renamedFile = HelloSpringUtils.getRenamedFile(handoutsSaveDirectory, lectureHandout.getOriginalFilename());
				//파일 저장
				lectureHandout.transferTo(renamedFile);
				//Attachment객체 생성
				Attachment attach = new Attachment();
				attach.setOriginalFilename(lectureHandout.getOriginalFilename());
				attach.setRenamedFilename(renamedFile.getName());
				attach.setRefContentsGroupCode(Attachment.CODE_LECTURE_HANDOUT);

				attachList.add(attach);
			}


			//1. 업무로직
			lecture.setAttachList(attachList);
			lecture.setRefMemberId(((Member)authentication.getPrincipal()).getMemberId());
			log.debug("lecture(필드값 Set 후) = {}", lecture);

			int result = lectureService.insertLecture(lecture);

			//2. 사용자 피드백
			String msg = result > 0 ? "게시글 등록 성공!" : "게시글 등록 실패!";
			redirectAttr.addFlashAttribute("msg", msg);

		} catch (IOException | IllegalStateException e) {
			log.error("첨부파일 등록 오류!", e);
			throw new AttachmentException("첨부파일 등록 오류!"); //Checked Exception은 throw로 바로 던질수 없으니, 커스팀 예외 객체를 만들어 던져준다.
		} catch (Exception e) {
			log.error("강의 등록 오류!", e);
			throw e;
		}

		return "redirect:/teacher/lectureEnroll.do";
	}
	
	 @GetMapping("/teacherProfile.do") 
	    public ModelAndView myProfile(HttpServletRequest request,
			  						  ModelAndView mav, 
			  						  Authentication authentication) {
		  
		// log.debug("request.isUserInRole('TEACHER') = {}", request.isUserInRole("TEACHER"));
		  
		  
		  try {
			  UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			  log.debug("userDetails = {}", userDetails);
			  // userDetails = Member(memberId=teacher, memberPw=$2a$10$X8GL750RHq/TpQh9hVPnd.Krj13dW5QlKAvUIbIIVI.dPVzPYUmd2,
			  //                       memberProfile=null, memberReProfile=null, authorities=[ROLE_TEACHER, ROLE_USER])
			  String memberId =  ((Member) userDetails).getMemberId();
			  log.debug("memberId = {}", memberId); //memberId = teacher

				//lecture.setRefMemberId(((Member)authentication.getPrincipal()).getMemberId());
				//log.debug("myProfileMethod@lecture = {}", lecture);
			  List<Lecture> list = lectureService.selectMyLecture(memberId);
			  log.debug("list = {}", list);
		     // list = [Lecture(lectureNo=0, refLecCatNo=0, refMemberId=null, lectureName=테스트1,

			  mav.addObject("list",list);
			  mav.setViewName("/teacher/teacherProfile");

		  }catch(Exception e) {
			  throw e;
		  }

		  return mav;

		  }


}
