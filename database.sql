--
-- PostgreSQL database dump
--

\restrict 2ElS8XraWMkRTvhbhmnGqUcfQza9gZp0KZ6tUEWcZ2wvX7oK7URC6nIzCJU4eF0

-- Dumped from database version 18.0
-- Dumped by pg_dump version 18.0

-- Started on 2026-04-07 12:44:27

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 224 (class 1259 OID 16810)
-- Name: Appointment; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."Appointment" (
    appointment_id integer NOT NULL,
    user_id integer,
    slot_id integer
);


ALTER TABLE public."Appointment" OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 16809)
-- Name: Appointment_appointment_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public."Appointment_appointment_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public."Appointment_appointment_id_seq" OWNER TO postgres;

--
-- TOC entry 5040 (class 0 OID 0)
-- Dependencies: 223
-- Name: Appointment_appointment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public."Appointment_appointment_id_seq" OWNED BY public."Appointment".appointment_id;


--
-- TOC entry 222 (class 1259 OID 16747)
-- Name: TimeSlots; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."TimeSlots" (
    slot_id integer NOT NULL,
    start_datetime timestamp without time zone NOT NULL,
    end_datetime timestamp without time zone,
    is_available boolean DEFAULT true
);


ALTER TABLE public."TimeSlots" OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 16746)
-- Name: TimeSlots_slot_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public."TimeSlots_slot_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public."TimeSlots_slot_id_seq" OWNER TO postgres;

--
-- TOC entry 5041 (class 0 OID 0)
-- Dependencies: 221
-- Name: TimeSlots_slot_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public."TimeSlots_slot_id_seq" OWNED BY public."TimeSlots".slot_id;


--
-- TOC entry 220 (class 1259 OID 16732)
-- Name: Users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."Users" (
    user_id integer NOT NULL,
    name character varying(100)[] NOT NULL,
    email character varying(150)[] NOT NULL,
    password character varying(255)[] NOT NULL
);


ALTER TABLE public."Users" OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 16731)
-- Name: Users_user_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public."Users_user_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public."Users_user_id_seq" OWNER TO postgres;

--
-- TOC entry 5042 (class 0 OID 0)
-- Dependencies: 219
-- Name: Users_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public."Users_user_id_seq" OWNED BY public."Users".user_id;


--
-- TOC entry 4869 (class 2604 OID 16813)
-- Name: Appointment appointment_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Appointment" ALTER COLUMN appointment_id SET DEFAULT nextval('public."Appointment_appointment_id_seq"'::regclass);


--
-- TOC entry 4867 (class 2604 OID 16750)
-- Name: TimeSlots slot_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."TimeSlots" ALTER COLUMN slot_id SET DEFAULT nextval('public."TimeSlots_slot_id_seq"'::regclass);


--
-- TOC entry 4866 (class 2604 OID 16735)
-- Name: Users user_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Users" ALTER COLUMN user_id SET DEFAULT nextval('public."Users_user_id_seq"'::regclass);


--
-- TOC entry 5034 (class 0 OID 16810)
-- Dependencies: 224
-- Data for Name: Appointment; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public."Appointment" (appointment_id, user_id, slot_id) FROM stdin;
\.


--
-- TOC entry 5032 (class 0 OID 16747)
-- Dependencies: 222
-- Data for Name: TimeSlots; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public."TimeSlots" (slot_id, start_datetime, end_datetime, is_available) FROM stdin;
1	2026-04-02 09:00:00	2026-04-02 10:00:00	t
\.


--
-- TOC entry 5030 (class 0 OID 16732)
-- Dependencies: 220
-- Data for Name: Users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public."Users" (user_id, name, email, password) FROM stdin;
5	{"karam khader"}	{karamkhader12345@gmail.com}	{karam123}
9	{j}	{hi@gmail.com}	{j2}
10	{"j j"}	{j@gmail.com}	{j2}
11	{"ayham yaseen"}	{ayhamyaseen100@gmail.com}	{ayham123}
\.


--
-- TOC entry 5043 (class 0 OID 0)
-- Dependencies: 223
-- Name: Appointment_appointment_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public."Appointment_appointment_id_seq"', 3, true);


--
-- TOC entry 5044 (class 0 OID 0)
-- Dependencies: 221
-- Name: TimeSlots_slot_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public."TimeSlots_slot_id_seq"', 1, true);


--
-- TOC entry 5045 (class 0 OID 0)
-- Dependencies: 219
-- Name: Users_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public."Users_user_id_seq"', 11, true);


--
-- TOC entry 4877 (class 2606 OID 16816)
-- Name: Appointment Appointment_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Appointment"
    ADD CONSTRAINT "Appointment_pkey" PRIMARY KEY (appointment_id);


--
-- TOC entry 4875 (class 2606 OID 16755)
-- Name: TimeSlots TimeSlots_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."TimeSlots"
    ADD CONSTRAINT "TimeSlots_pkey" PRIMARY KEY (slot_id);


--
-- TOC entry 4871 (class 2606 OID 16743)
-- Name: Users Users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Users"
    ADD CONSTRAINT "Users_pkey" PRIMARY KEY (user_id);


--
-- TOC entry 4879 (class 2606 OID 16818)
-- Name: Appointment slot_id_unique; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Appointment"
    ADD CONSTRAINT slot_id_unique UNIQUE (slot_id);


--
-- TOC entry 4873 (class 2606 OID 16745)
-- Name: Users unique_email; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Users"
    ADD CONSTRAINT unique_email UNIQUE (email);


--
-- TOC entry 4880 (class 2606 OID 16824)
-- Name: Appointment fk_slot_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Appointment"
    ADD CONSTRAINT fk_slot_id FOREIGN KEY (slot_id) REFERENCES public."TimeSlots"(slot_id);


--
-- TOC entry 4881 (class 2606 OID 16819)
-- Name: Appointment fk_user_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Appointment"
    ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES public."Users"(user_id);


-- Completed on 2026-04-07 12:44:27

--
-- PostgreSQL database dump complete
--

\unrestrict 2ElS8XraWMkRTvhbhmnGqUcfQza9gZp0KZ6tUEWcZ2wvX7oK7URC6nIzCJU4eF0

