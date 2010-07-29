@".\define.sql"
conn &&INOUT_USER/&&INOUT_PASS@&&TNS
exec sfw_processa_grants(p_owner => '&&INTEGRACAO_USER', p_tipo => 'R');

conn &&INTEGRACAO_USER/&&INTEGRACAO_PASS@&&TNS
exec sfw_processa_grants(p_owner => '&&INOUT_USER', p_tipo => 'R');

conn &&SOFT_USER/&&SOFT_USER@&&TNS
exec sfw_processa_grants(p_owner => '&&INTEGRACAO_USER', p_tipo => 'R');

conn &&IMPORT_USER/&&IMPORT_USER@&&TNS
exec sfw_processa_grants(p_owner => '&&INTEGRACAO_USER', p_tipo => 'R');

conn &&BROKER_USER/&&BROKER_USER@&&TNS
exec sfw_processa_grants(p_owner => '&&INTEGRACAO_USER', p_tipo => 'R');

conn &&EXPORT_USER/&&EXPORT_USER@&&TNS
exec sfw_processa_grants(p_owner => '&&INTEGRACAO_USER', p_tipo => 'R');

conn &&DRAWBACK_USER/&&DRAWBACK_USER@&&TNS
exec sfw_processa_grants(p_owner => '&&INTEGRACAO_USER', p_tipo => 'R');

conn &&CAMBIO_EXP_USER/&&CAMBIO_EXP_USER@&&TNS
exec sfw_processa_grants(p_owner => '&&INTEGRACAO_USER', p_tipo => 'R');

conn &&CAMBIO_IMP_USER/&&CAMBIO_IMP_USER@&&TNS
exec sfw_processa_grants(p_owner => '&&INTEGRACAO_USER', p_tipo => 'R');

