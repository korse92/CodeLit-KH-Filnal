<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%-- 로그인 검증용 --%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<fmt:requestEncoding value="utf-8"/>
<jsp:include page="/WEB-INF/views/common/header.jsp">
	<jsp:param value="강의 등록" name="title"/>
</jsp:include>
<!-- 컨텐츠 시작 -->
<!-- 개인 CSS, JS 위치 -->
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/ckeditor/ckeditor.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/fullcalendar-custom.css"/>

<!-- full Calendar -->
<!-- <link href='https://cdn.jsdelivr.net/npm/bootstrap@4.5.0/dist/css/bootstrap.css' rel='stylesheet' /> -->
<!-- <link href='https://cdn.jsdelivr.net/npm/@fortawesome/fontawesome-free@5.13.1/css/all.css' rel='stylesheet'>-->

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/lib/main.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/lib/main.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/lib/locales/ko.js"></script>

<!-- datepicker -->
<link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>

<!-- timepicker -->
<!-- <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/timepicker/1.3.5/jquery.timepicker.min.css">
<script src="//cdnjs.cloudflare.com/ajax/libs/timepicker/1.3.5/jquery.timepicker.min.js"></script>
-->
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/lib/jquery.timepicker.min.js" ></script><!-- 타이머js -->
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/resources/lib/jquery.timepicker.css" media=""/><!-- 타이머css -->

<!-- 강의 등록관련 js -->
<script src="${pageContext.request.contextPath}/resources/js/lectureEnroll.js"></script>

<style>
.form-group .row {
	margin-top: 1rem;
	margin-bottom: 1rem;
}

.form-group .form-label {
	margin-bottom: 0px;
}

img#thumbImage {
	width: 450px;
	height: 300px;
}

/* full Calendar */
#calendar {
	max-width: 1100px;
	margin: 0 auto;
}

.inputModal {
	width: 65%;
	margin-bottom: 10px;
	justify-content: end;
}
</style>

<div class="container">
	<div class="mt-5 mx-auto form-group" style="width:fit-content;">
		<form:form
			id="lectureEnrollFrm"
			name="lectureEnrollFrm"
			action="${pageContext.request.contextPath}/lecture/lectureEnroll.do"
			method="post"
			enctype="multipart/form-data">
			<div class="row justify-content-center">
				<!-- col-auto : 내부요소 크기에 맞게 컬럼 크기 맞춤 -->
				<div class="col-auto">
					<h2>강의 등록</h2>
				</div>
			</div>
			<div class="row">
				<div class="col-sm-2 align-self-center">
					<label class="form-label" for="lectureName">강의 제목</label>
				</div>
				<div class="col-sm-4">
					<select class="form-select" name="refLecCatNo" required>
						<option value="" disabled selected>카테고리 선택</option>
						<c:forEach items="${categoryList}" var="category">
						<option value="${category.no}">${category.name}</option>
						</c:forEach>
					</select>
				</div>
				<div class="col-sm-6">
					<input class="form-control" type="text" name="lectureName"
						id="lectureName" placeholder="강의 제목" required>
				</div>
			</div>
			<div class="row justify-content-between">
				<div class="col-sm-2 align-self-center">
					<label class="form-label">강의 종류</label>
				</div>
				<div class="col-sm-auto">
					<input class="form-check-input" type="radio" name="lectureType"	id="lectureType1" value="V" required>
					<label class="form-check-label me-3" for="lectureType1">일반 강의</label>
					<input class="form-check-input" type="radio" name="lectureType"	id="lectureType2" value="S">
					<label class="form-check-label"	for="lectureType2">스트리밍 강의</label>
				</div>
			</div>
			<div class="row">
				<div class="col-sm-2 align-self-center">
					<label class="form-label" for="lecturePrice">수강료</label>
				</div>
				<div class="col-sm">
					<input class="form-control" type="number" name="lecturePrice" min="0"
						id="lecturePrice" placeholder="무료 강의일 경우 0을 입력해주세요.(기본값 0)">
				</div>
			</div>
			<div class="row">
				<div class="col-sm-2 align-self-center">
					<label class="form-label" for="lectureHandout">첨부파일</label>
				</div>
				<div class="col-sm-10">
					<input class="form-control" type="file" name="lectureHandout"
						id="lectureHandout">
				</div>
			</div>
			<div class="row">
				<div class="col-sm-2 align-self-center">
					<label class="form-label" for="lectureThumbnail">썸네일</label>
				</div>
				<div class="col-sm-10">
					<img
						src="https://via.placeholder.com/450x300.png?text=Thumbnail+Image"
						class="img-thumbnail w-100" id="thumbImage" alt="썸네일 이미지">
					<input
						class="form-control d-none" type="file" name="lectureThumbnail"
						id="lectureThumbnail">
				</div>
			</div>
			<div class="row">
				<label class="form-label mb-2" for="">강의 소개글</label>
				<div class="col-sm">
					<textarea name="lectureIntro" id="lectureIntro" class="form-control"></textarea>
				</div>
			</div>
			<div class="selectedVideo row">
				<label class="form-label mb-2" for="lectureGuideline">가이드라인 (권장하는 하루에 들을 영상개수)</label>
				<div class="col-sm">
					<input class="form-control" type="number" name="lectureGuideline" min="1" max="10"
						id="lectureGuideline" placeholder="입력안할 시 기본값 1, 최대 10">
				</div>
			</div>

			<div class="selectedVideo row">
				<label class="form-label mb-2" for="">커리큘럼 등록 (스트리밍 강의일 경우 영상첨부 안하셔도 됩니다.)</label>
				<!-- <div class="row my-0 justify-content-end">
					<div class="col-auto">
						<button type="button" class="btn p-0" id="partAddBtn"><i class="fas fa-plus-square text-primary fs-3"></i></button>
						<button type="button" class="btn p-0" id="partDelBtn"><i class="fas fa-minus-square text-warning fs-3"></i></button>
					</div>
				</div> -->
				<div class="col-sm">
					<div class="d-flex flex-column align-items-start" id="inputCurriculum">
						<!--
						<div class="part-group w-100">
							<div class="input-group">
								<button type="button" class="btn p-0 me-2 partDelBtn"
										data-bs-toggle="tooltip" data-bs-placement="left" title="파트 삭제">
									<i class="fas fa-minus-square text-warning fs-3"></i>
								</button>
								<input type="text" class="partInput form-control my-1" placeholder="파트 제목 입력">
							</div>
							<div class="chapter-group ps-5">
								<div class="input-group">
									<button type="button" class="btn p-0 me-2 chapDelBtn"
											data-bs-toggle="tooltip" data-bs-placement="left" title="챕터 삭제">
										<i class="fas fa-minus-square text-warning fs-3"></i>
									</button>
									<input type="text" class="chapterInput form-control my-1" placeholder="챕터 제목 입력">
								</div>
								<input type="file" class="form-control form-control-sm" name="chapterVideo" accept="video/*">
							</div>
							<button type="button" class="btn chapAddBtn ms-5 mt-1 p-0"
									data-bs-toggle="tooltip" data-bs-placement="left" title="챕터 추가">
								<i class="fas fa-plus-square text-primary fs-3"></i>
							</button>
						</div>
						-->
						<button type="button" class="btn p-0 mt-1 partAddBtn"
								data-bs-toggle="tooltip" data-bs-placement="left" title="파트 추가">
							<i class="fas fa-plus-square text-primary fs-3"></i>
						</button>
					</div>
					<input type="button" class="" value="테스트" id="curtest"/>
				</div>
				<input type="hidden" name="curriculum" />
				<input type="hidden" name="videoChapNoArr" />
			</div>

			<div id="selectedStreaming" class="d-none">
				<div class="row">
					<label class="form-label mb-2" for="">강의일정</label>
					<div class="col-sm">
						<input type="hidden" name="streamingDates" />
						<div id='calendar'></div>
						<input type="button" class="" value="테스트" id="calTest" />
					</div>
				</div>

				<div class="row">
					<div class="col-sm-2 align-self-center">
						<label class="form-label" for="streamingStartTime">시작 시간</label>
					</div>
					<div class="col-sm">
						<input class="timepicker form-control" type="text" name="streamingStartTime" id="streamingStartTime" value="" maxlength="10" />
					</div>
				</div>

				<div class="row">
					<div class="col-sm-2 align-self-center">
						<label class="form-label" for="streamingEndTime">종료 시간</label>
					</div>
					<div class="col-sm">
						<input class="timepicker form-control" type="text" name="streamingEndTime" id="streamingEndTime" value="" maxlength="10"/>
					</div>
				</div>
			</div>

			<div class="row form-group justify-content-end">
				<div class="col-sm-auto">
					<input class="btn btn-warning btn" type="reset" value="리셋">
					<input class="btn btn-primary" type="submit" value="등록 요청">
				</div>
			</div>
		</form:form>
	</div>

	<!-- 스트리밍 강의 등록 모달 -->
	<div class="modal fade" tabindex="-1" id="eventModal" aria-hidden="true" aria-labelledby="eventModalLabel" aria-hidden="true">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<!-- modal-header -->
				<div class="modal-header">
					<h4 class="modal-title"></h4>
				</div>

				<!-- modal-body -->
				<div class="modal-body">
					<div class="row mb-3">
						<label for="title" class="col-sm-2 col-form-label">일정명</label>
						<div class="col-sm-10">
							<input type="text" class="form-control" id="title" required="required" />
						</div>
					</div>

					<div class="row mb-3">
						<label for="start" class="col-sm-2 col-form-label">시작</label>
						<div class="col-sm-10 form-group">
							<input type="text" class="datepicker form-control" id="startDate" name="startDate"/>
						</div>
					</div>

					<div class="row mb-3">
						<label for="end" class="col-sm-2 col-form-label">끝</label>
						<div class="col-sm-10 form-group">
							<input type="text" class="datepicker form-control" id="endDate" name="endDate"/>
						</div>
					</div>
				</div>

				<!-- modal-footer -->
				<div class="modal-footer modalBtnContainer-addEvent d-none">
					<button type="button" class="btn btn-warning" data-bs-dismiss="modal">취소</button>
					<button type="button" class="btn btn-primary" id="saveEvent">저장</button>
				</div>
				<div class="modal-footer modalBtnContainer-modifyEvent d-none">
					<button type="button" class="btn btn-warning" data-bs-dismiss="modal">닫기</button>
					<button type="button" class="btn btn-danger" id="deleteEvent">삭제</button>
					<button type="button" class="btn btn-primary" id="updateEvent">저장</button>
				</div>

			</div><!-- /.modal-content -->
		</div><!-- /.modal-dialog -->
	</div><!-- /.modal -->
</div><!-- container -->

<!-- 컨텐츠 끝 -->
<jsp:include page="/WEB-INF/views/common/footer.jsp"></jsp:include>
