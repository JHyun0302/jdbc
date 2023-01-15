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