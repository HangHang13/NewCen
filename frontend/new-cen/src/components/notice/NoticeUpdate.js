import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';

import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import Modal from 'react-bootstrap/Modal';

import { BASE_URL, NOTICE } from '../common/config/host-config';
import { getToken } from '../common/util/login-util';

import Editor from '../common/EditorComponent';
import CommentRadioBtn from '../common/CommentRadioBtn';

import './css/NoticeUpdate.css';

// 공지사항 수정
const NoticeUpdate = () => {
    var noticeId = useParams().noticeId;

    const API_BASE_URL = BASE_URL + NOTICE;
    const ACCESS_TOKEN = getToken();

    // 공지사항 api 데이터 
    const [noticeContents, setNoticeContents] = useState([]);

    const [modal, setModal] = useState(false); 
    const [desc, setDesc] = useState('');

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
                // if (res.status === 403) {
                //     alert('로그인이 필요한 서비스입니다');

                //     window.location.href = '/';
                //     return;
                // } 
                // else if (res.status === 500) {
                //     alert('서버가 불안정합니다');
                //     return;
                // }
                return res.json();
            })
            .then(result => {
                console.log(result.noticeDetails[0]);
                setNoticeContents(result.noticeDetails[0]);
            });
    }, [API_BASE_URL, noticeId]);

    // 모달 닫기
    const handleClose = () => {
        setModal(false);
    };

    // 취소 클릭 시 경고 모달
    const handleShowCancelModal = () => {
        setModal(true);     // 모달 열기
    }
    
    function onEditorChange(value) {
        setDesc(value);
    };

    // 공지사항 내용 페이지로
    const navigate = useNavigate();
    const onNoticeContentPage = () => {
        const path = `/notice/${noticeId}`;
        navigate(path);
    };

    return (
        <>
            <div id='notice_update_div'>
                <div className='justify'>
                    <Form>
                        <Form.Group className='mb-3'>
                            <Form.Control id='notice_update_title' autoFocus type='text' defaultValue={noticeContents.boardTitle}/>
                        </Form.Group>
                    </Form>

                     {/* 라디오 버튼 */} 
                    <CommentRadioBtn comment={noticeContents.boardCommentIs}/>
                </div>

                <div>
                    <Editor value={desc} onChange={onEditorChange} defaultValue={noticeContents.boardContent}/>
                </div>

                <div id='notice_update_footer_div'>
                    <Button className='btn_gray btn_size_100' onClick={handleShowCancelModal}>취소</Button>
                    <Button className='btn_orange btn_size_100' id='notice_update_btn'>수정</Button>
                </div>
            </div>

{/* Modal */}
            <Modal show={modal} onHide={handleClose} id="noticee_update_modal">
                <Modal.Body id='notice_update_modal_body'>
                    <div id='notice_update_modal_content'>
                        작성하신 글을 취소하시면 저장되지 않습니다. <br />
                        그래도 취소하시겠습니까?
                    </div>

                    <div id="notice_update_modal_content">
                        <Button className='btn_gray notice_btn btn_size_100' onClick={handleClose}>
                            아니오
                        </Button>
                        <Button className='btn_orange notice_btn btn_size_100' id="notice_update_btn" onClick={onNoticeContentPage}>
                            네
                        </Button>
                    </div>
                </Modal.Body>
            </Modal>
        </>
    )
}

export default NoticeUpdate;
