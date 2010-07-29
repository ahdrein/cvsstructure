conn &&INTEGRACAO_USER/&&INTEGRACAO_PASS@&&TNS; 
 
declare 
-- 
v_s_CharacterSet VARCHAR2(160); 
-- 
begin 
  -- 
 select VALUE 
   into v_s_CharacterSet 
   from NLS_DATABASE_PARAMETERS 
  where PARAMETER = 'NLS_CHARACTERSET'; 
 -- 
 if v_s_CharacterSet in ('UTF8', 'AL32UTF8') then 
   -- 
   execute immediate 'alter session set nls_length_semantics=char'; 
   -- 
 end if; 
 -- 
end; 
/ 
