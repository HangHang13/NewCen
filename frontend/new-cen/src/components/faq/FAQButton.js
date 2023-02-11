import React from 'react';
import Button from 'react-bootstrap/Button';

import './css/FAQButton.css';

// 자주 묻는 질문 버튼들
const FAQButton = () => {

    const onInsertPage = () => {
        window.location.href = "/faq/insert";
    }

    return (
        <div className='justify'>
            <div id='faq_button_txt'>자주 묻는 질문</div>
            <div id='faq_button_group'>
                <Button className='btn_indigo' id='faq_button_date_asc'>날짜 순</Button>
                <Button className='btn_indigo' id='faq_button_like_asc'>좋아요 순</Button>
                <Button className='btn_indigo' id='faq_button_commente_asc'>댓글 순</Button>
                <Button onClick={onInsertPage} className='btn_orange btn_size_100' id='faq_button_insert'>글쓰기</Button>
            </div>
        </div>      
    )
}

export default FAQButton;
