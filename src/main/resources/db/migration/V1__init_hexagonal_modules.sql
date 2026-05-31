create table erp_family (
    id bigserial primary key,
    external_id varchar(100) not null unique,
    tutor_name varchar(150) not null,
    tutor_email varchar(150) not null,
    active boolean not null default true,
    created_at timestamp not null default now()
);

create table erp_student_ref (
    id bigserial primary key,
    family_id bigint not null references erp_family(id),
    external_id varchar(100) not null unique,
    full_name varchar(200) not null,
    active boolean not null default true
);

create table erp_account_status (
    id bigserial primary key,
    family_id bigint not null references erp_family(id),
    period_code varchar(20) not null,
    expected_amount numeric(12,2) not null,
    paid_amount numeric(12,2) not null default 0,
    debt_amount numeric(12,2) not null default 0,
    status varchar(20) not null,
    due_date date,
    unique (family_id, period_code)
);

create table erp_discount_assignment (
    id bigserial primary key,
    family_id bigint not null references erp_family(id),
    discount_code varchar(80) not null,
    percentage numeric(5,2) not null,
    assigned_at timestamp not null default now()
);

create table doc_document (
    id bigserial primary key,
    family_id bigint not null references erp_family(id),
    document_type varchar(50) not null,
    storage_key varchar(255) not null,
    status varchar(20) not null,
    uploaded_by varchar(100) not null,
    uploaded_at timestamp not null default now()
);

create table doc_document_review (
    id bigserial primary key,
    document_id bigint not null references doc_document(id),
    reviewer_user varchar(100) not null,
    status varchar(20) not null,
    reason varchar(255),
    reviewed_at timestamp not null default now()
);

create table doc_receipt_invoice (
    id bigserial primary key,
    family_id bigint not null references erp_family(id),
    period_code varchar(20) not null,
    amount numeric(12,2) not null,
    storage_key varchar(255) not null,
    created_at timestamp not null default now()
);

create table bi_collection_fact_daily (
    id bigserial primary key,
    business_date date not null,
    payment_method varchar(30) not null,
    amount numeric(12,2) not null,
    external_payment_id varchar(100) not null,
    unique (external_payment_id)
);

create table bi_delinquency_snapshot (
    id bigserial primary key,
    snapshot_date date not null,
    families_in_arrears int not null,
    total_debt numeric(14,2) not null
);

create table int_inbox_event (
    id bigserial primary key,
    event_id varchar(120) not null unique,
    event_type varchar(80) not null,
    payload text not null,
    processed_at timestamp not null default now()
);

create table int_outbox_event (
    id bigserial primary key,
    event_id varchar(120) not null unique,
    event_type varchar(80) not null,
    aggregate_type varchar(80) not null,
    aggregate_id varchar(120) not null,
    payload text not null,
    status varchar(20) not null default 'PENDING',
    created_at timestamp not null default now()
);

create table audit_log (
    id bigserial primary key,
    action_code varchar(80) not null,
    entity_type varchar(80) not null,
    entity_id varchar(120) not null,
    actor varchar(100) not null,
    details varchar(500),
    created_at timestamp not null default now()
);
