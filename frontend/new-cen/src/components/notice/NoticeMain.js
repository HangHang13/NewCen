import React from 'react';
import Table from 'react-bootstrap/Table';
import Button from 'react-bootstrap/Button';

import './css//NoticeMain.css';

const NoticeMain = () => {

    const onInsertPage = () => {
        window.location.href = "/notice/insert";
    }

    return (
        <>
            <div id='notice_btn_main'>
                <div id='notice_insert_btn_main'>
                    <Button onClick={onInsertPage} className='btn_orange btn_size_100'>추가</Button>
                </div>

                <div id='notice_table_main'>
                    <Table responsive id='notice_table'>
                        <thead>
                            <tr id='notice_main_thead'>
                                <th width="10%">번호</th>
                                <th width="20%">제목</th>
                                <th width="15%">날짜</th>
                                <th width="15%">작성자</th>
                            </tr>
                        </thead>
                        <tbody>
                        
                        </tbody>
                    </Table >   
                </div>
            </div>
        </>
    )
}

export default NoticeMain;