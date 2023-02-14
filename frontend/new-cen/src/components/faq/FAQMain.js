import React, {useEffect, useState} from 'react';
import Table from 'react-bootstrap/Table';
import FAQButton from './FAQButton';

import './css/FAQMain.css';

import { BASE_URL, FAQ } from '../common/config/host-config';
import { useNavigate } from "react-router-dom";

// 자주 묻는 질문 메인
const FAQMain = () => {

    // 자주 묻는 질문 번호
    let i = 0;

    const API_BASE_URL = BASE_URL + FAQ;

    // 자주 묻는 질문 리스트
    const [faqList, setFaqList] = useState([]);


    // 자주 묻는 질문 상세
    const navigate = useNavigate();
    const handleFaqContent = (boardId) => {
        const path = `/faq/${boardId}`;
        navigate(path);
    }

    // 렌더링 되자마자 할 일 => FAQ api GET 목록 호출
    useEffect(() => {
        
        fetch(API_BASE_URL)
        .then(res => {
            if (res.status === 500) {
                alert('서버가 불안정합니다');
                return;
            }

            return res.json();
        })
        .then(res => {
            setFaqList(res.content);
        });
    }, [API_BASE_URL]);


    return (
        <>
            <div id='faq_btn_main'>
                <FAQButton />
                <div id='faq_table_main'>
                    <Table responsive id='faq_table'>
                        <thead>
                            <tr id='faq_main_thead'>
                                <th width="10%">번호</th>
                                <th width="20%">제목</th>
                                <th width="15%">날짜</th>
                                <th width="15%">작성자</th>
                            </tr>
                        </thead>
                        <tbody>
                            {
                            !faqList.empty &&  
                                faqList.map((item) => {
                                    i++;
                                    return (
                                        <tr key={item.boardId} id='faq_main_rows' onClick={() => handleFaqContent(item.boardId)}>
                                            <td width="10%">{i}</td>
                                            <td width="20%">{item.boardTitle}</td>
                                            <td width="15%">{item.boardCreatedate}</td>
                                            <td width="15%">{item.boardWriter}</td>
                                        </tr>
                                    )
                                })
                            } 
                        </tbody>
                    </Table >   
                </div>
            </div>
        </>
    )
}

export default FAQMain;