package com.fisa.infra.board.service;

import com.fisa.infra.account.domain.Account;
import com.fisa.infra.account.repository.jpa.AccountRepository;
import com.fisa.infra.board.domain.Board;
import com.fisa.infra.board.domain.dto.BoardDTO;
import com.fisa.infra.board.domain.dto.UploadFile;
import com.fisa.infra.board.repository.jpa.BoardRepository;
import com.fisa.infra.board.repository.querydsl.QueryBoardRepository;
import com.fisa.infra.picture.domain.Picture;
import com.fisa.infra.picture.repository.CommonPictureRepository;
import com.fisa.infra.upload.FileStore;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {
	
	 private final BoardRepository boardRepository;
	 private final AccountRepository accountRepository;
	 private final QueryBoardRepository queryBoardRepository;
	 private final CommonPictureRepository commonPictureRepository;

	 private ModelMapper mapper = new ModelMapper();
	private final FileStore fileStore;

	 /**
	  * 게시글 작성
	  * @param boardDTO
	  * @return 저장된 글
	  */
	 @Transactional
	 public Board writeBoard(String loginId, BoardDTO boardDTO) throws RuntimeException, IOException {
		 Account account = accountRepository.findAccountByLoginId("김어진")
	                .orElseThrow(() -> new RuntimeException("해당 로그인 아이디를 가진 회원이 존재하지 않습니다."));

	     Board board = Board.builder()
	    		 .account(account)
	    		 .content(boardDTO.getContent())
	    		 .title(boardDTO.getTitle())
	    		 .build();

		 Board save = boardRepository.save(board);

		 List<UploadFile> uploadFileList = fileStore.storeAllFile(boardDTO.getUploadFile());
		 for (int i = 0; i < uploadFileList.size(); i++) {
			 Picture picture = Picture.savePicture(uploadFileList.get(i).getStoreFileName(), uploadFileList.get(i).getUploadFileName());
			 picture.addBoard(save);
			 commonPictureRepository.save(picture);
		 }
		 return save;
	 }

	public List<BoardDTO> getAllBoard() {
		List<Board> boardAll = boardRepository.findAll();
		List<BoardDTO> collect = boardAll.stream().map(b -> mapper.map(b, BoardDTO.class)).collect(Collectors.toList());
		log.info("{} ", boardAll.size());

		if(boardAll.isEmpty()){
			return Collections.emptyList();
		}

		return collect;
	}

    public BoardDTO getBoardById(Long id) {
		 Optional<BoardDTO> board = queryBoardRepository.queryFindBoardById(id);
		 return board.map(b -> mapper.map(b, BoardDTO.class)).orElse(null);
    }
}
