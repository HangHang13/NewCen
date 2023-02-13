import React, { useEffect, useState } from 'react';

import Table from 'react-bootstrap/Table';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import Modal from 'react-bootstrap/Modal';

import MessageButton from './MessageButton';

import { BASE_URL, MESSAGE } from '../common/config/host-config';
import { getToken } from '../common/util/login-util';

import './css/MessageMain.css';

// 메세지 메인
const MessageMain = () => {

    const API_BASE_URL = BASE_URL + MESSAGE;
    const ACCESS_TOKEN = getToken();

    // 메세지 api 데이터 
    const [messages, setMessages] = useState([]);

    // headers
    const headerInfo = {
        'content-type': 'application/json',
        'Authorization': 'Bearer ' + ACCESS_TOKEN
    }

    // 전체 선택 여부
    const [selectAll, setSelectAll] = useState(true);

    // 전체 선택 / 해제
    const handleSelectAll = () => {
        let i = 0;
        const check_boxes = document.querySelectorAll(".message_select_checkbox");
        console.log(selectAll);
        console.log(check_boxes);
        if(selectAll) {
            while(i < check_boxes.length) {
                console.log(check_boxes[i]);
                check_boxes[i].checked = true;
                i++;
            }
            setSelectAll(false);
        }else {
            while(i < check_boxes.length) {
                check_boxes[i].checked = false;
                i++;
            }
            setSelectAll(true);
        }
    }

    // 받은 메세지 모달
    const [receiveModal, setReceiveModal] = useState(false); 

    // 답장하기 모달
    const [replyModal, setReplyModal] = useState(false); 

    // 모달 닫기
    const handleClose = () => {
        setReceiveModal(false);
        setReplyModal(false);
    };

    // 제목 클릭 시 받은 메세지 상세 모달
    const handleShowReceiveModal = () => {
        setReceiveModal(true);     // 받은 메세지 모달 열기
    }

    // 답장하기 상세 모달
    const handleShowReplyModal = () => {
        setReceiveModal(false);     // 받은 메세지 모달은 닫기
        setReplyModal(true);     // 답장하기 모달 열기
    }

    // 렌더링 되자마자 할 일 => 메세지 api GET 목록 호출
    useEffect(() => {
        fetch(API_BASE_URL, {
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
                console.log(result);
                setMessages(result.messages);
            });
    }, [API_BASE_URL]);

    return (
        <>
            <div id='message_btn_main'>
                <MessageButton />
                <div id='message_table_main'>
                    <Table responsive id='message_table'>
                        <thead>
                            <tr id='message_main_thead'>
                                <th width="10%">보낸 사람</th>
                                <th width="20%">제목</th>
                                <th width="15%">날짜</th>
                                <th width="15%" id='message_main_all_select' onClick={handleSelectAll}>전체 선택</th>
                            </tr>
                        </thead>
                        <tbody>

                            {/* db 연결하여 map 함수 이용 */}
                            {/* {
                                messages.map((item) => {
                                    return (
                                        <tr key={item.messageId} id='message_main_tbody'>
                                            <td>{item.messageId}</td>
                                            <th id='message_main_tbody_th' onClick={handleShowReceiveModal}>{item.messageTitle}</th>
                                            <td>{item.messageSenddate}</td>
                                            <td><input type='checkbox' id='message_select_checkbox' /></td>
                                        </tr>
                                    )
                                })     
                            } */}
                            
                                {/* 아래는 임시 데이터 */}
                                <tr id='message_main_tbody'>
                                    <td>허진영</td>
                                    <th onClick={handleShowReceiveModal} id='message_main_tbody_th'>연봉은 대체 얼마인가요?</th>
                                    <td>2023-01-18</td>
                                    <td><input type='checkbox' className='message_select_checkbox' /></td>
                                </tr>
                                <tr id='message_main_tbody'>
                                    <td>이진행</td>
                                    <th onClick={handleShowReceiveModal} id='message_main_tbody_th'>회사 발령은 언제 결정되나요?</th>
                                    <td>2023-01-18</td>
                                    <td><input type='checkbox' className='message_select_checkbox' /></td>
                                </tr>
                        </tbody>
                    </Table >   
                </div>
            </div>

            {/* Modal */}
            <Modal show={receiveModal} onHide={handleClose} id="message_send_modal">
                <Modal.Body>
                    <div id='message_send_modal_body'>
                        <Form.Group className='mb-3'>
                            <Form.Label id='message_form_label'>보낸 사람</Form.Label>
                            <Form.Control type='text' className='message_form_control'/>
                        </Form.Group>
                        <Form.Group className='mb-3'>
                            <Form.Label id='message_form_label'>제목</Form.Label>
                            <Form.Control type='text' className='message_form_control'/>
                        </Form.Group>
                        <Form.Group className='mb-3'>
                            <Form.Label id='message_form_label'>내용</Form.Label>
                            <textarea rows="5" className="form-control" id='message_send_content'/>
                        </Form.Group>
                    </div>

                    <div id='message_send_modal_footer'>
                        <Button className='btn_gray notice_btn btn_size_100' onClick={handleClose}>
                            닫기
                        </Button>
                        <Button className='btn_orange notice_btn btn_size_100' id="notice_content_delete_btn" onClick={handleShowReplyModal}>
                            답장하기
                        </Button>
                    </div>
                </Modal.Body>
            </Modal>

            <Modal show={replyModal} onHide={handleClose} id="message_send_modal">
                <Modal.Body>
                    <div id='message_send_modal_body'>
                        <Form.Group className='mb-3'>
                            <Form.Label id='message_form_label'>받는 사람</Form.Label>
                            <Form.Control autoFocus type='text' className='message_form_control' placeholder='받는 사람'/>
                        </Form.Group>
                        <Form.Group className='mb-3'>
                            <Form.Label id='message_form_label'>제목</Form.Label>
                            <Form.Control type='text' className='message_form_control' placeholder='제목'/>
                        </Form.Group>
                        <Form.Group className='mb-3'>
                            <Form.Label id='message_form_label'>내용</Form.Label>
                            <textarea rows="5" className="form-control" id='message_send_content'/>
                        </Form.Group>
                    </div>

                    <div id='message_send_modal_footer'>
                        <Button className='btn_gray notice_btn btn_size_100' onClick={handleClose}>
                            취소
                        </Button>
                        <Button className='btn_orange notice_btn btn_size_100' id="notice_content_delete_btn">
                            보내기
                        </Button>
                    </div>
                </Modal.Body>
            </Modal>
        </>
    )
}

export default MessageMain