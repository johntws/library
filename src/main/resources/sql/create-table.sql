CREATE TABLE BOOK (
    DELETED BOOLEAN NOT NULL,
    RETURNED BOOLEAN NOT NULL,
    CREATED_DATE TIMESTAMP(6) DEFAULT NULL,
    MODIFIED_DATE TIMESTAMP(6) DEFAULT NULL,
    AUTHOR VARCHAR(255) DEFAULT NULL,
    CREATED_BY VARCHAR(255) DEFAULT NULL,
    ISBN VARCHAR(255) NOT NULL,
    MODIFIED_BY VARCHAR(255) DEFAULT NULL,
    TITLE VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (ISBN)
);

CREATE TABLE MEMBER (
    DELETED BOOLEAN NOT NULL,
    CREATED_DATE TIMESTAMP(6) DEFAULT NULL,
    ID BIGINT NOT NULL,
    MODIFIED_DATE TIMESTAMP(6) DEFAULT NULL,
    CREATED_BY VARCHAR(255) DEFAULT NULL,
    EMAIL VARCHAR(255) DEFAULT NULL,
    MODIFIED_BY VARCHAR(255) DEFAULT NULL,
    NAME VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (ID)
);

CREATE TABLE COPY (
    BORROWED BOOLEAN NOT NULL,
    DELETED BOOLEAN NOT NULL,
    CREATED_DATE TIMESTAMP(6) DEFAULT NULL,
    ID BIGINT NOT NULL,
    MODIFIED_DATE TIMESTAMP(6) DEFAULT NULL,
    CREATED_BY VARCHAR(255) DEFAULT NULL,
    ISBN VARCHAR(255) DEFAULT NULL,
    MODIFIED_BY VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (ID),
    FOREIGN KEY (ISBN) REFERENCES BOOK(ISBN)
);

CREATE TABLE LOAN (
    DELETED BOOLEAN NOT NULL,
    DUE_DATE DATE DEFAULT NULL,
    ISSUE_DATE DATE DEFAULT NULL,
    RETURN_DATE DATE DEFAULT NULL,
    COPY_ID BIGINT DEFAULT NULL,
    MEMBER_ID BIGINT DEFAULT NULL,
    CREATED_DATE TIMESTAMP(6) DEFAULT NULL,
    ID BIGINT NOT NULL,
    MODIFIED_DATE TIMESTAMP(6) DEFAULT NULL,
    CREATED_BY VARCHAR(255) DEFAULT NULL,
    MODIFIED_BY VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (ID),
    FOREIGN KEY (COPY_ID) REFERENCES COPY(ID),
    FOREIGN KEY (MEMBER_ID) REFERENCES MEMBER(ID)
);
