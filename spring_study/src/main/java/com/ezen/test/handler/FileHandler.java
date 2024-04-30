package com.ezen.test.handler;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ezen.test.domain.FileVO;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

@Slf4j
@Component
public class FileHandler {

	// 실제 파일이 저장되는 경로
	private final String UP_DIR = "D:\\W\\Spring\\_myProject\\_java\\_fileUpload";
	
	public List<FileVO> uploadFiles(MultipartFile[] files) {
		// return 객체 생성(직접 생성할 경우, arraylist로 생성)
		List<FileVO> flist = new ArrayList<FileVO>();
				
		// MultipartFile[]을 받아, FileVO 형태의 List로 생성 후 리턴
		// 오늘 날짜로 경로생성(가변형태로 저장) : "년월일" 폴더를 구성
		
		// 오늘 날짜의 경로
		LocalDate date = LocalDate.now();
		String today = date.toString();
		log.info("> FileHandler today {}", today);
		// 오늘 날짜를 폴더로 구성
		today = today.replace("-", File.separator);
		// ex) D:\\W\\Spring\\_myProject\\_java\\_fileUpload\\2024\\04\\29
		File folders = new File(UP_DIR, today);
		// 폴더가 없으면 생성
		// 폴더 생성 > mkdir (폴더 1개 생성), mkdirs(경로의 하위 폴더까지 구조로 생성; 여러개를 생성)
		// exists : 있는지 없는지 확인하는 속성
		if(!folders.exists()) {
			folders.mkdirs();	// 폴더 생성 명령처리
		}
		
		// 리스트 생성
		for(MultipartFile file : files) {
			FileVO fvo = new FileVO();
			fvo.setSave_dir(today);
			fvo.setFile_size(file.getSize());	// getSize는 return이 long-
			
			// getOriginalFilename() : 파일에 대한 이름 <<< 경로 + 파일명(파일 경로를 포함하는 케이스도 있다.)
			String originalFileName = file.getOriginalFilename();
			// 실제 파일명만 추출
			String onlyFileName = originalFileName.substring(originalFileName.lastIndexOf(File.separator)+1);
			fvo.setFile_name(onlyFileName);
			
			// UUID 생성
			UUID uuid = UUID.randomUUID();		// UUID 객체로 받으므로, string으로 형변환필요
			String uuidStr = uuid.toString();
			fvo.setUuid(uuidStr);
			
		// ------------------- fvo Setting 완료 ------------------- //
			// bno, file_type

			// 디스크에 저장 처리
			// 디스크에 저장할 파일 객체 생성
			// uuid_fileName / uuid_th_fileName
			// fullFileName : 저장할 파일명
			String fullFileName = uuidStr+"_"+onlyFileName;
			
			// 저장파일 객체 생성
			File storeFile = new File(folders, fullFileName);
			
			// 저장할 때 > 저장경로 또는 파일이 없다면, IOException 발생
			try {
				
				file.transferTo(storeFile);			// 저장 처리
				
				// 파일 타입을 결정(파일이 이미지인지 여부를 체크)하여, 썸네일 처리
				if(isImageFile(storeFile)) {
					fvo.setFile_type(1);
					
					//썸네일 생성
					File thumbNail = new File(folders, uuidStr+"_th_"+onlyFileName);
					Thumbnails.of(storeFile).size(75, 75).toFile(thumbNail);
				}
				
				
			} catch (Exception e) {
				log.info(">>> file 저장 에러");
				e.printStackTrace();
			}		
			
			// list에 fvo 추가
			flist.add(fvo);
			
		}
		
		
		return flist;
	}

	// tika를 활용하여 파일 형식 체크 >>> 이미지 파일이 맞는지 확인하는 작업
	public boolean isImageFile(File storeFile) throws IOException {
		String mimeType = new Tika().detect(storeFile);		// imagename/png, imagename/jpg ... 등으로 표현
		return mimeType.startsWith("image") ? true : false;				// image 문구 체크
	}
	
	
	
	
	
}