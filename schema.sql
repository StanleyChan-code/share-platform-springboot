

--
-- Name: education_level; Type: TYPE; Schema: public; Owner: supabase_admin
--

CREATE TYPE public.education_level AS ENUM (
    'bachelor',
    'master',
    'phd',
    'postdoc',
    'professor',
    'other'
);


ALTER TYPE public.education_level OWNER TO supabase_admin;

--
-- Name: id_type; Type: TYPE; Schema: public; Owner: supabase_admin
--

CREATE TYPE public.id_type AS ENUM (
    'national_id',
    'passport',
    'other'
);


ALTER TYPE public.id_type OWNER TO supabase_admin;

--
-- Name: institution_type; Type: TYPE; Schema: public; Owner: supabase_admin
--

CREATE TYPE public.institution_type AS ENUM (
    'hospital',
    'university',
    'research_center',
    'lab',
    'government',
    'enterprise',
    'other'
);


ALTER TYPE public.institution_type OWNER TO supabase_admin;

--
-- Name: output_type; Type: TYPE; Schema: public; Owner: supabase_admin
--

CREATE TYPE public.output_type AS ENUM (
    'paper',
    'patent',
    'publication',
    'software',
    'project',
    'invention_patent',
    'utility_patent',
    'software_copyright',
    'other_award'
);


ALTER TYPE public.output_type OWNER TO supabase_admin;

--
-- Name: user_role; Type: TYPE; Schema: public; Owner: supabase_admin
--

CREATE TYPE public.user_role AS ENUM (
    'public_visitor',
    'registered_researcher',
    'data_provider',
    'institution_supervisor',
    'platform_admin'
);


ALTER TYPE public.user_role OWNER TO supabase_admin;
--
-- Name: analysis_results; Type: TABLE; Schema: public; Owner: supabase_admin
--

CREATE TABLE public.analysis_results (
                                         id uuid DEFAULT gen_random_uuid() NOT NULL,
                                         dataset_id uuid NOT NULL,
                                         total_rows integer NOT NULL,
                                         total_columns integer NOT NULL,
                                         analysis_date timestamp with time zone DEFAULT now() NOT NULL,
                                         overall_missing_rate numeric,
                                         memory_usage_mb numeric,
                                         correlations jsonb,
                                         field_mappings jsonb,
                                         unit_conversions jsonb,
                                         analysis_metadata jsonb,
                                         created_at timestamp with time zone DEFAULT now() NOT NULL,
                                         updated_at timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.analysis_results OWNER TO supabase_admin;

--
-- Name: applicant_role; Type: TYPE; Schema: public; Owner: supabase_admin
--

CREATE TYPE public.applicant_role AS ENUM (
    'team_researcher',
    'collaborative_researcher'
);


ALTER TYPE public.applicant_role OWNER TO supabase_admin;

--
-- Name: application_status; Type: TYPE; Schema: public; Owner: supabase_admin
--

CREATE TYPE public.application_status AS ENUM (
    'submitted',
    'under_review',
    'approved',
    'denied'
);


ALTER TYPE public.application_status OWNER TO supabase_admin;

--
-- Name: applications; Type: TABLE; Schema: public; Owner: supabase_admin
--

CREATE TABLE public.applications (
                                     id uuid DEFAULT gen_random_uuid() NOT NULL,
                                     dataset_id uuid NOT NULL,
                                     applicant_id uuid NOT NULL,
                                     supervisor_id uuid,
                                     project_title text NOT NULL,
                                     project_description text NOT NULL,
                                     funding_source text,
                                     purpose text NOT NULL,
                                     status public.application_status DEFAULT 'submitted'::public.application_status,
                                     admin_notes text,
                                     provider_notes text,
                                     submitted_at timestamp with time zone DEFAULT now() NOT NULL,
                                     reviewed_at timestamp with time zone,
                                     approved_at timestamp with time zone,
                                     approval_document_url text,
                                     applicant_role public.applicant_role DEFAULT 'team_researcher'::public.applicant_role NOT NULL,
                                     applicant_type text
);

ALTER TABLE ONLY public.applications REPLICA IDENTITY FULL;


ALTER TABLE public.applications OWNER TO supabase_admin;

--
-- Name: audit_logs; Type: TABLE; Schema: public; Owner: supabase_admin
--

CREATE TABLE public.audit_logs (
                                   id uuid DEFAULT gen_random_uuid() NOT NULL,
                                   user_id uuid,
                                   action text NOT NULL,
                                   resource_type text NOT NULL,
                                   resource_id uuid,
                                   details jsonb,
                                   ip_address inet,
                                   created_at timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.audit_logs OWNER TO supabase_admin;

--
-- Name: dataset_statistics; Type: TABLE; Schema: public; Owner: supabase_admin
--

CREATE TABLE public.dataset_statistics (
                                           id uuid DEFAULT gen_random_uuid() NOT NULL,
                                           dataset_id uuid NOT NULL,
                                           variable_name text NOT NULL,
                                           variable_type text NOT NULL,
                                           mean_value numeric,
                                           std_deviation numeric,
                                           percentage numeric,
                                           missing_count integer,
                                           total_count integer,
                                           created_at timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.dataset_statistics OWNER TO supabase_admin;

--
-- Name: dataset_versions; Type: TABLE; Schema: public; Owner: supabase_admin
--

CREATE TABLE public.dataset_versions (
                                         id uuid DEFAULT gen_random_uuid() NOT NULL,
                                         dataset_id uuid NOT NULL,
                                         version_number text NOT NULL,
                                         published_date timestamp with time zone DEFAULT now() NOT NULL,
                                         changes_description text,
                                         file_url text,
                                         data_dict_url text,
                                         terms_agreement_url text,
                                         created_at timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.dataset_versions OWNER TO supabase_admin;

--
-- Name: dataset_type; Type: TYPE; Schema: public; Owner: supabase_admin
--

CREATE TYPE public.dataset_type AS ENUM (
    'cohort',
    'case_control',
    'cross_sectional',
    'rct',
    'registry',
    'biobank',
    'omics',
    'wearable'
);


ALTER TYPE public.dataset_type OWNER TO supabase_admin;
--
-- Name: datasets; Type: TABLE; Schema: public; Owner: supabase_admin
--

CREATE TABLE public.datasets (
                                 id uuid DEFAULT gen_random_uuid() NOT NULL,
                                 title_cn text NOT NULL,
                                 description text NOT NULL,
                                 type public.dataset_type NOT NULL,
                                 category text,
                                 provider_id uuid NOT NULL,
                                 supervisor_id uuid,
                                 start_date date,
                                 end_date date,
                                 record_count integer,
                                 variable_count integer,
                                 keywords text[],
                                 subject_area_id uuid,
                                 file_url text,
                                 data_dict_url text,
                                 approved boolean DEFAULT false,
                                 published boolean DEFAULT false,
                                 search_count integer DEFAULT 0,
                                 share_all_data boolean DEFAULT false,
                                 created_at timestamp with time zone DEFAULT now() NOT NULL,
                                 updated_at timestamp with time zone DEFAULT now() NOT NULL,
                                 dataset_leader text,
                                 data_collection_unit text,
                                 contact_person text,
                                 contact_info text,
                                 demographic_fields jsonb,
                                 outcome_fields jsonb,
                                 terms_agreement_url text,
                                 sampling_method text,
                                 version_number text DEFAULT '1.0'::text,
                                 first_published_date timestamp with time zone,
                                 current_version_date timestamp with time zone DEFAULT now(),
                                 parent_dataset_id uuid,
                                 principal_investigator text
);

ALTER TABLE ONLY public.datasets REPLICA IDENTITY FULL;


ALTER TABLE public.datasets OWNER TO supabase_admin;

--
-- Name: institutions; Type: TABLE; Schema: public; Owner: supabase_admin
--

CREATE TABLE public.institutions (
                                     id uuid DEFAULT gen_random_uuid() NOT NULL,
                                     username text NOT NULL,
                                     full_name text NOT NULL,
                                     short_name text,
                                     type public.institution_type NOT NULL,
                                     contact_person text NOT NULL,
                                     contact_id_type public.id_type NOT NULL,
                                     contact_id_number text NOT NULL,
                                     contact_phone text NOT NULL,
                                     contact_email text NOT NULL,
                                     verified boolean DEFAULT false,
                                     created_at timestamp with time zone DEFAULT now() NOT NULL,
                                     updated_at timestamp with time zone DEFAULT now() NOT NULL,
                                     user_id uuid
);


ALTER TABLE public.institutions OWNER TO supabase_admin;

--
-- Name: COLUMN institutions.user_id; Type: COMMENT; Schema: public; Owner: supabase_admin
--

COMMENT ON COLUMN public.institutions.user_id IS '关联的用户ID，指向机构管理员账户';


--
-- Name: institutions_public; Type: VIEW; Schema: public; Owner: supabase_admin
--

CREATE VIEW public.institutions_public AS
SELECT institutions.id,
       institutions.full_name,
       institutions.short_name,
       institutions.type,
       institutions.verified,
       institutions.created_at
FROM public.institutions
WHERE (institutions.verified = true);


ALTER TABLE public.institutions_public OWNER TO supabase_admin;

--
-- Name: research_outputs; Type: TABLE; Schema: public; Owner: supabase_admin
--

CREATE TABLE public.research_outputs (
                                         id uuid DEFAULT gen_random_uuid() NOT NULL,
                                         dataset_id uuid NOT NULL,
                                         submitter_id uuid NOT NULL,
                                         type public.output_type NOT NULL,
                                         title text NOT NULL,
                                         abstract text,
                                         patent_number text,
                                         citation_count integer DEFAULT 0,
                                         publication_url text,
                                         file_url text,
                                         created_at timestamp with time zone DEFAULT now() NOT NULL,
                                         approved boolean DEFAULT false,
                                         approved_by uuid,
                                         approved_at timestamp with time zone,
                                         rejection_reason text,
                                         journal text
);

--
-- Name: research_subjects; Type: TABLE; Schema: public; Owner: supabase_admin
--

CREATE TABLE public.research_subjects (
                                          id uuid DEFAULT gen_random_uuid() NOT NULL,
                                          name text NOT NULL,
                                          name_en text,
                                          description text,
                                          active boolean DEFAULT true,
                                          created_at timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.research_subjects OWNER TO supabase_admin;

--
-- Name: user_roles; Type: TABLE; Schema: public; Owner: supabase_admin
--

CREATE TABLE public.user_roles (
                                   id uuid DEFAULT gen_random_uuid() NOT NULL,
                                   user_id uuid NOT NULL,
                                   role public.user_role NOT NULL,
                                   created_at timestamp with time zone DEFAULT now() NOT NULL,
                                   created_by uuid
);

ALTER TABLE ONLY public.user_roles REPLICA IDENTITY FULL;


ALTER TABLE public.user_roles OWNER TO supabase_admin;

--
-- Name: users; Type: TABLE; Schema: public; Owner: supabase_admin
--

CREATE TABLE public.users (
                              id uuid NOT NULL,
                              username text NOT NULL,
                              real_name text NOT NULL,
                              id_type public.id_type,
                              id_number text,
                              education public.education_level,
                              title text,
                              field text,
                              institution_id uuid,
                              phone text,
                              email text NOT NULL,
                              role public.user_role DEFAULT 'public_visitor'::public.user_role NOT NULL,
                              supervisor_id uuid,
                              created_at timestamp with time zone DEFAULT now() NOT NULL,
                              updated_at timestamp with time zone DEFAULT now() NOT NULL
);

ALTER TABLE ONLY public.users REPLICA IDENTITY FULL;


--
-- Name: create_institution_user_profile(uuid, text, text, text, text, public.id_type, text, uuid); Type: FUNCTION; Schema: public; Owner: supabase_admin
--

CREATE FUNCTION public.create_institution_user_profile(user_id uuid, user_username text, user_real_name text, user_email text, user_phone text, user_id_type public.id_type, user_id_number text, user_institution_id uuid) RETURNS void
    LANGUAGE plpgsql SECURITY DEFINER
    SET search_path TO 'public'
    AS $$
BEGIN
  -- Check if caller is a platform admin
  IF NOT public.has_role(auth.uid(), 'platform_admin') THEN
    RAISE EXCEPTION 'Only platform admins can create user profiles';
END IF;

  -- Insert the user profile
INSERT INTO public.users (
    id,
    username,
    real_name,
    email,
    phone,
    id_type,
    id_number,
    institution_id
) VALUES (
             user_id,
             user_username,
             user_real_name,
             user_email,
             user_phone,
             user_id_type,
             user_id_number,
             user_institution_id
         );

-- Also insert the role into user_roles table
INSERT INTO public.user_roles (
    user_id,
    role,
    created_by
) VALUES (
             user_id,
             'institution_supervisor',
             auth.uid()
         );
END;
$$;


ALTER FUNCTION public.create_institution_user_profile(user_id uuid, user_username text, user_real_name text, user_email text, user_phone text, user_id_type public.id_type, user_id_number text, user_institution_id uuid) OWNER TO supabase_admin;

--
-- Name: create_user_profile(uuid, text, text, text, text, public.id_type, text); Type: FUNCTION; Schema: public; Owner: supabase_admin
--

CREATE FUNCTION public.create_user_profile(user_id uuid, user_username text, user_real_name text, user_email text, user_phone text, user_id_type public.id_type, user_id_number text) RETURNS void
    LANGUAGE plpgsql SECURITY DEFINER
    SET search_path TO 'public'
    AS $$
BEGIN
  -- Check if caller is a platform admin
  IF NOT public.has_role(auth.uid(), 'platform_admin') THEN
    RAISE EXCEPTION 'Only platform admins can create user profiles';
END IF;

  -- Insert the user profile
INSERT INTO public.users (
    id,
    username,
    real_name,
    email,
    phone,
    id_type,
    id_number,
    role
) VALUES (
             user_id,
             user_username,
             user_real_name,
             user_email,
             user_phone,
             user_id_type,
             user_id_number,
             'institution_supervisor'
         );

-- Also insert the role into user_roles table
INSERT INTO public.user_roles (
    user_id,
    role,
    created_by
) VALUES (
             user_id,
             'institution_supervisor',
             auth.uid()
         );
END;
$$;


ALTER FUNCTION public.create_user_profile(user_id uuid, user_username text, user_real_name text, user_email text, user_phone text, user_id_type public.id_type, user_id_number text) OWNER TO supabase_admin;

--
-- Name: get_institution_supervisor_id(); Type: FUNCTION; Schema: public; Owner: supabase_admin
--

CREATE FUNCTION public.get_institution_supervisor_id() RETURNS uuid
    LANGUAGE sql SECURITY DEFINER
    SET search_path TO 'public'
    AS $$
SELECT institution_id
FROM public.users
WHERE id = auth.uid()
  AND role = 'institution_supervisor'
    LIMIT 1;
$$;


ALTER FUNCTION public.get_institution_supervisor_id() OWNER TO supabase_admin;

--
-- Name: get_public_stats(); Type: FUNCTION; Schema: public; Owner: supabase_admin
--

CREATE FUNCTION public.get_public_stats() RETURNS jsonb
    LANGUAGE sql SECURITY DEFINER
    SET search_path TO 'public'
    AS $$
SELECT jsonb_build_object(
               'users_count', (SELECT COUNT(*) FROM users),
               'datasets_count', (SELECT COUNT(*) FROM datasets WHERE published = true AND approved = true),
               'applications_count', (SELECT COUNT(*) FROM applications),
               'outputs_count', (SELECT COUNT(*) FROM research_outputs)
       );
$$;


ALTER FUNCTION public.get_public_stats() OWNER TO supabase_admin;

--
-- Name: has_role(uuid, public.user_role); Type: FUNCTION; Schema: public; Owner: supabase_admin
--

CREATE FUNCTION public.has_role(_user_id uuid, _role public.user_role) RETURNS boolean
    LANGUAGE sql STABLE SECURITY DEFINER
    SET search_path TO 'public'
    AS $$
SELECT EXISTS (
    SELECT 1
    FROM public.user_roles
    WHERE user_id = _user_id
      AND role = _role
)
           $$;


ALTER FUNCTION public.has_role(_user_id uuid, _role public.user_role) OWNER TO supabase_admin;

--
-- Name: is_admin(); Type: FUNCTION; Schema: public; Owner: supabase_admin
--

CREATE FUNCTION public.is_admin() RETURNS boolean
    LANGUAGE sql SECURITY DEFINER
    SET search_path TO 'public'
    AS $$
SELECT public.has_role(auth.uid(), 'platform_admin');
$$;


ALTER FUNCTION public.is_admin() OWNER TO supabase_admin;

--
-- Name: is_authenticated_user(); Type: FUNCTION; Schema: public; Owner: supabase_admin
--

CREATE FUNCTION public.is_authenticated_user() RETURNS boolean
    LANGUAGE plpgsql STABLE SECURITY DEFINER
    AS $$
BEGIN
RETURN auth.uid() IS NOT NULL;
END;
$$;


ALTER FUNCTION public.is_authenticated_user() OWNER TO supabase_admin;

--
-- Name: is_current_user_institution_supervisor(); Type: FUNCTION; Schema: public; Owner: supabase_admin
--

CREATE FUNCTION public.is_current_user_institution_supervisor() RETURNS boolean
    LANGUAGE sql SECURITY DEFINER
    SET search_path TO 'public'
    AS $$
SELECT EXISTS (
    SELECT 1
    FROM public.user_roles
    WHERE user_id = auth.uid()
      AND role = 'institution_supervisor'
);
$$;


ALTER FUNCTION public.is_current_user_institution_supervisor() OWNER TO supabase_admin;

--
-- Name: is_institution_supervisor(uuid); Type: FUNCTION; Schema: public; Owner: supabase_admin
--

CREATE FUNCTION public.is_institution_supervisor(institution_id uuid) RETURNS boolean
    LANGUAGE sql SECURITY DEFINER
    SET search_path TO 'public'
    AS $$
SELECT EXISTS (
    SELECT 1
    FROM public.users
    WHERE id = auth.uid()
      AND institution_id = is_institution_supervisor.institution_id
      AND role = 'institution_supervisor'
);
$$;


ALTER FUNCTION public.is_institution_supervisor(institution_id uuid) OWNER TO supabase_admin;

--
-- Name: set_first_published_date(); Type: FUNCTION; Schema: public; Owner: supabase_admin
--

CREATE FUNCTION public.set_first_published_date() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF NEW.published = true AND NEW.approved = true AND OLD.first_published_date IS NULL THEN
        NEW.first_published_date = now();
END IF;
RETURN NEW;
END;
$$;


ALTER FUNCTION public.set_first_published_date() OWNER TO supabase_admin;

--
-- Name: update_research_output_approval(uuid, boolean, text); Type: FUNCTION; Schema: public; Owner: supabase_admin
--

CREATE FUNCTION public.update_research_output_approval(output_id uuid, is_approved boolean, reject_reason text DEFAULT NULL::text) RETURNS void
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
BEGIN
UPDATE public.research_outputs
SET
    approved = is_approved,
    approved_by = auth.uid(),
    approved_at = NOW(),
    rejection_reason = CASE WHEN is_approved THEN NULL ELSE reject_reason END
WHERE id = output_id;
END;
$$;


ALTER FUNCTION public.update_research_output_approval(output_id uuid, is_approved boolean, reject_reason text) OWNER TO supabase_admin;

--
-- Name: update_updated_at_column(); Type: FUNCTION; Schema: public; Owner: supabase_admin
--

CREATE FUNCTION public.update_updated_at_column() RETURNS trigger
    LANGUAGE plpgsql
    SET search_path TO 'public'
    AS $$
BEGIN
    NEW.updated_at = now();
RETURN NEW;
END;
$$;


ALTER FUNCTION public.update_updated_at_column() OWNER TO supabase_admin;


ALTER TABLE ONLY public.research_outputs REPLICA IDENTITY FULL;


ALTER TABLE public.research_outputs OWNER TO supabase_admin;

--
-- Name: COLUMN research_outputs.approved; Type: COMMENT; Schema: public; Owner: supabase_admin
--

COMMENT ON COLUMN public.research_outputs.approved IS 'Whether the research output has been approved by admins';


--
-- Name: COLUMN research_outputs.approved_by; Type: COMMENT; Schema: public; Owner: supabase_admin
--

COMMENT ON COLUMN public.research_outputs.approved_by IS 'The admin who approved/rejected the research output';


--
-- Name: COLUMN research_outputs.approved_at; Type: COMMENT; Schema: public; Owner: supabase_admin
--

COMMENT ON COLUMN public.research_outputs.approved_at IS 'When the research output was approved/rejected';


--
-- Name: COLUMN research_outputs.rejection_reason; Type: COMMENT; Schema: public; Owner: supabase_admin
--

COMMENT ON COLUMN public.research_outputs.rejection_reason IS 'Reason for rejection if applicable';


--
-- Name: COLUMN research_outputs.journal; Type: COMMENT; Schema: public; Owner: supabase_admin
--

COMMENT ON COLUMN public.research_outputs.journal IS 'Journal name for research papers';



ALTER TABLE public.users OWNER TO supabase_admin;

--
-- Name: analysis_results_pkey; Type: CONSTRAINT; Schema: public; Owner: supabase_admin
--

ALTER TABLE ONLY public.analysis_results
    ADD CONSTRAINT analysis_results_pkey PRIMARY KEY (id);


--
-- Name: applications_pkey; Type: CONSTRAINT; Schema: public; Owner: supabase_admin
--

ALTER TABLE ONLY public.applications
    ADD CONSTRAINT applications_pkey PRIMARY KEY (id);


--
-- Name: audit_logs_pkey; Type: CONSTRAINT; Schema: public; Owner: supabase_admin
--

ALTER TABLE ONLY public.audit_logs
    ADD CONSTRAINT audit_logs_pkey PRIMARY KEY (id);


--
-- Name: dataset_statistics_pkey; Type: CONSTRAINT; Schema: public; Owner: supabase_admin
--

ALTER TABLE ONLY public.dataset_statistics
    ADD CONSTRAINT dataset_statistics_pkey PRIMARY KEY (id);


--
-- Name: dataset_versions_dataset_id_version_number_key; Type: CONSTRAINT; Schema: public; Owner: supabase_admin
--

ALTER TABLE ONLY public.dataset_versions
    ADD CONSTRAINT dataset_versions_dataset_id_version_number_key UNIQUE (dataset_id, version_number);


--
-- Name: dataset_versions_pkey; Type: CONSTRAINT; Schema: public; Owner: supabase_admin
--

ALTER TABLE ONLY public.dataset_versions
    ADD CONSTRAINT dataset_versions_pkey PRIMARY KEY (id);


--
-- Name: datasets_pkey; Type: CONSTRAINT; Schema: public; Owner: supabase_admin
--

ALTER TABLE ONLY public.datasets
    ADD CONSTRAINT datasets_pkey PRIMARY KEY (id);


--
-- Name: institutions_pkey; Type: CONSTRAINT; Schema: public; Owner: supabase_admin
--

ALTER TABLE ONLY public.institutions
    ADD CONSTRAINT institutions_pkey PRIMARY KEY (id);


--
-- Name: institutions_username_key; Type: CONSTRAINT; Schema: public; Owner: supabase_admin
--

ALTER TABLE ONLY public.institutions
    ADD CONSTRAINT institutions_username_key UNIQUE (username);


--
-- Name: research_outputs_pkey; Type: CONSTRAINT; Schema: public; Owner: supabase_admin
--

ALTER TABLE ONLY public.research_outputs
    ADD CONSTRAINT research_outputs_pkey PRIMARY KEY (id);


--
-- Name: research_subjects_pkey; Type: CONSTRAINT; Schema: public; Owner: supabase_admin
--

ALTER TABLE ONLY public.research_subjects
    ADD CONSTRAINT research_subjects_pkey PRIMARY KEY (id);


--
-- Name: user_roles_pkey; Type: CONSTRAINT; Schema: public; Owner: supabase_admin
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT user_roles_pkey PRIMARY KEY (id);


--
-- Name: user_roles_user_id_role_key; Type: CONSTRAINT; Schema: public; Owner: supabase_admin
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT user_roles_user_id_role_key UNIQUE (user_id, role);


--
-- Name: users_pkey; Type: CONSTRAINT; Schema: public; Owner: supabase_admin
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: users_username_key; Type: CONSTRAINT; Schema: public; Owner: supabase_admin
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_username_key UNIQUE (username);


--
-- Name: idx_datasets_data_dict_url; Type: INDEX; Schema: public; Owner: supabase_admin
--

CREATE INDEX idx_datasets_data_dict_url ON public.datasets USING btree (data_dict_url);


--
-- Name: idx_datasets_file_url; Type: INDEX; Schema: public; Owner: supabase_admin
--

CREATE INDEX idx_datasets_file_url ON public.datasets USING btree (file_url);


--
-- Name: idx_datasets_parent_id; Type: INDEX; Schema: public; Owner: supabase_admin
--

CREATE INDEX idx_datasets_parent_id ON public.datasets USING btree (parent_dataset_id);


--
-- Name: idx_institutions_user_id; Type: INDEX; Schema: public; Owner: supabase_admin
--

CREATE INDEX idx_institutions_user_id ON public.institutions USING btree (user_id);


--
-- Name: idx_research_outputs_approved; Type: INDEX; Schema: public; Owner: supabase_admin
--

CREATE INDEX idx_research_outputs_approved ON public.research_outputs USING btree (approved);


--
-- Name: idx_research_outputs_submitter_id; Type: INDEX; Schema: public; Owner: supabase_admin
--

CREATE INDEX idx_research_outputs_submitter_id ON public.research_outputs USING btree (submitter_id);


--
-- Name: idx_user_roles_role; Type: INDEX; Schema: public; Owner: supabase_admin
--

CREATE INDEX idx_user_roles_role ON public.user_roles USING btree (role);


--
-- Name: idx_user_roles_user_id; Type: INDEX; Schema: public; Owner: supabase_admin
--

CREATE INDEX idx_user_roles_user_id ON public.user_roles USING btree (user_id);


--
-- Name: analysis_results update_analysis_results_updated_at; Type: TRIGGER; Schema: public; Owner: supabase_admin
--

CREATE TRIGGER update_analysis_results_updated_at BEFORE UPDATE ON public.analysis_results FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();


--
-- Name: datasets update_datasets_updated_at; Type: TRIGGER; Schema: public; Owner: supabase_admin
--

CREATE TRIGGER update_datasets_updated_at BEFORE UPDATE ON public.datasets FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();


--
-- Name: datasets update_first_published_date; Type: TRIGGER; Schema: public; Owner: supabase_admin
--

CREATE TRIGGER update_first_published_date BEFORE UPDATE ON public.datasets FOR EACH ROW EXECUTE FUNCTION public.set_first_published_date();


--
-- Name: institutions update_institutions_updated_at; Type: TRIGGER; Schema: public; Owner: supabase_admin
--

CREATE TRIGGER update_institutions_updated_at BEFORE UPDATE ON public.institutions FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();


--
-- Name: users update_users_updated_at; Type: TRIGGER; Schema: public; Owner: supabase_admin
--

CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON public.users FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();


--
-- Name: analysis_results analysis_results_dataset_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: supabase_admin
--

ALTER TABLE ONLY public.analysis_results
    ADD CONSTRAINT analysis_results_dataset_id_fkey FOREIGN KEY (dataset_id) REFERENCES public.datasets(id) ON DELETE CASCADE;


--
-- Name: applications applications_applicant_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: supabase_admin
--

ALTER TABLE ONLY public.applications
    ADD CONSTRAINT applications_applicant_id_fkey FOREIGN KEY (applicant_id) REFERENCES public.users(id);

--
-- Name: applications applications_dataset_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: supabase_admin
--

ALTER TABLE ONLY public.applications
    ADD CONSTRAINT applications_dataset_id_fkey FOREIGN KEY (dataset_id) REFERENCES public.datasets(id);


--
-- Name: applications applications_supervisor_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: supabase_admin
--

ALTER TABLE ONLY public.applications
    ADD CONSTRAINT applications_supervisor_id_fkey FOREIGN KEY (supervisor_id) REFERENCES public.users(id);


--
-- Name: audit_logs audit_logs_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: supabase_admin
--

ALTER TABLE ONLY public.audit_logs
    ADD CONSTRAINT audit_logs_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: dataset_statistics dataset_statistics_dataset_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: supabase_admin
--

ALTER TABLE ONLY public.dataset_statistics
    ADD CONSTRAINT dataset_statistics_dataset_id_fkey FOREIGN KEY (dataset_id) REFERENCES public.datasets(id) ON DELETE CASCADE;


--
-- Name: dataset_versions dataset_versions_dataset_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: supabase_admin
--

ALTER TABLE ONLY public.dataset_versions
    ADD CONSTRAINT dataset_versions_dataset_id_fkey FOREIGN KEY (dataset_id) REFERENCES public.datasets(id) ON DELETE CASCADE;


--
-- Name: datasets datasets_parent_dataset_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: supabase_admin
--

ALTER TABLE ONLY public.datasets
    ADD CONSTRAINT datasets_parent_dataset_id_fkey FOREIGN KEY (parent_dataset_id) REFERENCES public.datasets(id);


--
-- Name: datasets datasets_provider_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: supabase_admin
--

ALTER TABLE ONLY public.datasets
    ADD CONSTRAINT datasets_provider_id_fkey FOREIGN KEY (provider_id) REFERENCES public.users(id);


--
-- Name: datasets datasets_subject_area_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: supabase_admin
--

ALTER TABLE ONLY public.datasets
    ADD CONSTRAINT datasets_subject_area_id_fkey FOREIGN KEY (subject_area_id) REFERENCES public.research_subjects(id);


--
-- Name: datasets datasets_supervisor_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: supabase_admin
--

ALTER TABLE ONLY public.datasets
    ADD CONSTRAINT datasets_supervisor_id_fkey FOREIGN KEY (supervisor_id) REFERENCES public.users(id);


--
-- Name: institutions institutions_user_id_fkey1; Type: FK CONSTRAINT; Schema: public; Owner: supabase_admin
--

ALTER TABLE ONLY public.institutions
    ADD CONSTRAINT institutions_user_id_fkey1 FOREIGN KEY (user_id) REFERENCES auth.users(id);


--
-- Name: research_outputs research_outputs_approved_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: supabase_admin
--

ALTER TABLE ONLY public.research_outputs
    ADD CONSTRAINT research_outputs_approved_by_fkey FOREIGN KEY (approved_by) REFERENCES public.users(id);


--
-- Name: research_outputs research_outputs_dataset_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: supabase_admin
--

ALTER TABLE ONLY public.research_outputs
    ADD CONSTRAINT research_outputs_dataset_id_fkey FOREIGN KEY (dataset_id) REFERENCES public.datasets(id);


--
-- Name: research_outputs research_outputs_submitter_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: supabase_admin
--

ALTER TABLE ONLY public.research_outputs
    ADD CONSTRAINT research_outputs_submitter_id_fkey FOREIGN KEY (submitter_id) REFERENCES public.users(id);


--
-- Name: user_roles user_roles_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: supabase_admin
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT user_roles_created_by_fkey FOREIGN KEY (created_by) REFERENCES auth.users(id);


--
-- Name: user_roles user_roles_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: supabase_admin
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT user_roles_user_id_fkey FOREIGN KEY (user_id) REFERENCES auth.users(id) ON DELETE CASCADE;


--
-- Name: users users_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: supabase_admin
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_id_fkey FOREIGN KEY (id) REFERENCES auth.users(id) ON DELETE CASCADE;


--
-- Name: users users_institution_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: supabase_admin
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_institution_id_fkey FOREIGN KEY (institution_id) REFERENCES public.institutions(id);


--
-- Name: users users_supervisor_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: supabase_admin
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_supervisor_id_fkey FOREIGN KEY (supervisor_id) REFERENCES public.users(id);
