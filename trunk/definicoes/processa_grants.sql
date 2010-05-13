@".\define.sql"
conn &&INOUT_USER/&&INOUT_PASS@&&TNS
exec sfw_processa_grants(p_owner => '&&INTEGRACAO_USER');

conn &&INTEGRACAO_USER/&&INTEGRACAO_PASS@&&TNS
exec sfw_processa_grants(p_owner => '&&INOUT_USER');

