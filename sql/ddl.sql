drop table member if exists cascade;
create table member
(
    member_id varchar(10),
    money     integer not null default 0,
    primary key (member_id)
);

-- 삽입
insert into member(name)
values ('spring')

-- 모두 삭제
delete
from member

--예제 스키마
drop table member if exists;
create table member
(
    member_id varchar(10),
    money     integer not null default 0,
    primary key (member_id)
);

set
autocommit true;
//자동 커밋 모드 설정
insert into member(member_id, money) values ('data1',10000);
//자동 커밋
insert into member(member_id, money) values ('data2',10000);
//자동 커밋

set autocommit false;
//수동 커밋 모드 설정
insert into member(member_id, money) values ('data3',10000);
insert into member(member_id, money)
values ('data4', 10000);
commit;
//수동 커밋

