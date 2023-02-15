import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';


import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import Modal from 'react-bootstrap/Modal';

import { BASE_URL, NOTICE, AWS } from '../common/config/host-config';
import { getToken, getUserId } from '../common/util/login-util';

import NoticeComment from './NoticeComment';

import './css/NoticeContent.css';

// 공지사항 내용
const NoticeContent = () => {
    var noticeId = useParams().noticeId;
    
    const API_BASE_URL = BASE_URL + NOTICE;
    const API_AWS_URL = BASE_URL + AWS;

    const ACCESS_TOKEN = getToken();        // 토큰값
    const USER_ID = getUserId();
    
    // 공지사항 api 데이터 
    const [noticeContents, setNoticeContents] = useState([]);
    const [noticeFiles, setNoticeFiles] = useState([]);
    const [noticeFileCount, setNoticeFileCount] = useState(0);  // 파일 개수
    const [isComment, setIsComment] = useState('');     // 수정(NoticeUpdate) 페이지로 보낼 댓글여부 데이터

    const [modal, setModal] = useState(false); 

    // headers
    const headerInfo = {
        'content-type': 'application/json',
        'Authorization': 'Bearer ' + ACCESS_TOKEN
    }

    // 렌더링 되자마자 할 일 => 공지사항 api GET 목록 호출
    useEffect(() => {
        fetch(`${API_BASE_URL}/${noticeId}`, {
            method: 'GET',
            headers: headerInfo
        })
            .then(res => {
                if (res.status === 406) {
                    if (ACCESS_TOKEN === '') {
                        alert('로그인이 필요한 서비스입니다');
                        window.location.href = '/join';
                    } else {
                        alert('오류가 발생했습니다. 잠시 후 다시 이용해주세요');
                        return;
                    }
                    return;
                } 
                else if (res.status === 500) {
                    alert('서버가 불안정합니다');
                    return;
                }
                return res.json();
            })
            .then(result => {
                if (!!result) {
                    setNoticeContents(result.noticeDetails[0]);
                    setIsComment(result.noticeDetails[0]["boardCommentIs"]);

                    if (result.boardFileEntityList.length !== 0) {
                        setNoticeFileCount(result.boardFileEntityList.length);
                        setNoticeFiles(result.boardFileEntityList);
                    }
                }
            });
    }, [API_BASE_URL]);

    // 파일 클릭 시 다운로드
    const commentFileDown = (filePath) => {
        fetch(`${API_AWS_URL}/files/${filePath}`, {
            method: 'GET',
            headers: headerInfo,
        })
        .then(res => {
            if (res.status === 404) {
                alert('다시 시도해주세요');
                return;
            }
            else if (res.status === 406) {
                alert('오류가 발생했습니다. 잠시 후 다시 이용해주세요');
                return;
            } 
            else if (res.status === 500) {
                alert('서버가 불안정합니다');
                return;
            }
            else {
                window.location.href = res.url;
            }
        })
    }

    // 모달 닫기
    const handleClose = () => {
        setModal(false);
    };

    // 삭제 클릭 시 경고 모달
    const handleShowDeleteModal = () => {
        setModal(true);     // 모달 열기
    }

    // 공지사항 삭제 서버 요청 (DELETE)
    const handleDeleteNotice = () => {
        fetch(`${API_BASE_URL}/${noticeId}`, {
            method: 'DELETE',
            headers: headerInfo,
        })
        .then(res => {
            if (res.status === 404) {
                alert('다시 시도해주세요');
                return;
            }
            else if (res.status === 406) {
                if (ACCESS_TOKEN === '') {
                    alert('로그인이 필요한 서비스입니다');
                    window.location.href = '/join';
                } else {
                    alert('오류가 발생했습니다. 잠시 후 다시 이용해주세요');
                    return;
                }
                return;
            } 
            else if (res.status === 500) {
                alert('서버가 불안정합니다');
                return;
            }
            else {
                window.location.href = "/notice";       // 공지사항 목록 페이지로 이동
            }
        })
    }

    // 공지사항 수정 페이지로
    const navigate = useNavigate();
    const onUpdatePage = () => {
        const path = `/notice/update/${noticeId}`;
        navigate(path, {
            state: {
                comment: isComment      // NoticeUpdate.js에 댓글여부 값 보내기
            }
        });
    };

    // 공지사항 목록 페이지로
    const onNoticePage = () => {
        const path = `/notice`;
        navigate(path);
    };
       
    // 댓글 허용 여부에 따라 게시물 내용의 높이가 달라짐
    if (noticeContents.boardCommentIs === 'ON') {
        document.getElementById('notice_contents').style.height = '420px';
    } else if (noticeContents.boardCommentIs === 'OFF'){
        document.getElementById('notice_contents').style.height = '600px';
    }

    return (
        <>
            <div id='notice_content_main'>
                <div className='justify'>
                    <div>
                        <Form id='notice_content_title'>
                            {noticeContents.boardTitle}
                        </Form>

                        <div id='notice_content_write'>
                            작성자 : {noticeContents.boardWriter} | 작성일 : {noticeContents.createDate}
                        </div>
                    </div>

                    <>
                        {/* 게시물 등록한 사람인 경우에만 '수정','삭제' 버튼 보이도록 */}
                        {USER_ID === noticeContents.userId
                        ? 
                            <div id='notice_content_body_div'>
                                <Button onClick={onUpdatePage} className='btn_gray btn_size_100'>수정</Button>
                                <Button onClick={handleShowDeleteModal} className='btn_orange btn_size_100' id='notice_content_delete_btn'>삭제</Button>
                                <Button onClick={onNoticePage} className='btn_indigo btn_size_100' id='notice_content_list'>목록</Button>
                            </div>
                            :
                            <div id='notice_content_body_div'>
                                <Button onClick={onNoticePage} className='btn_indigo btn_size_100'>목록</Button>
                            </div>
                        }
                    </>                    
                </div>

                {/* 공지사항 파일 */}
                {noticeFiles.length !== 0 &&
                    <div id='notice_content_file_txt'>
                        첨부파일({noticeFileCount})
                        {
                            noticeFiles.map((item) => {
                                return (
                                    <span key={item.boardFileId} onClick={() => commentFileDown(item.boardFilePath)} id='notice_content_file_data'>
                                        | {item.boardFileName}
                                    </span>   
                                )
                            })
                        }   
                    </div>
                }

                {/* dangerouslySetInnerHTML : String형태를 html로 */}
                <div>
                    <Form id='notice_contents'
                        dangerouslySetInnerHTML={{
                            __html: noticeContents.boardContent
                        }} 
                    />
                </div>

                {/* 댓글 */}
                { noticeContents.boardCommentIs === 'ON'
                    ? 
                    (
                        <>
                            <NoticeComment noticeId = {noticeId} />
                        </>
                    )
                    : ''
                }
            </div>

            {/* Modal */}
            <Modal show={modal} onHide={handleClose} id="notice_delete_modal">
                <Modal.Body id='notice_delete_modal_body'>
                    <div id='notice_delete_modal_content'>
                        공지사항을 삭제하시겠습니까?
                    </div>

                    <div id="notice_delete_modal_content">
                        <Button onClick={handleClose} className='btn_gray notice_btn btn_size_100'>
                            아니오
                        </Button>
                        <Button onClick={handleDeleteNotice} className='btn_orange notice_btn btn_size_100' id="notice_content_delete_btn">
                            네
                        </Button>
                    </div>
                </Modal.Body>
            </Modal>
        </>
    )
}

export default NoticeContent;
