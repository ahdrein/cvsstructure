@".\define.sql"
conn &&INOUT_USER/&&INOUT_PASS@&&TNS
exec processa_grants_sistema(p_id_sistema => 'IT');
exec processa_grants_sistema(p_id_sistema => 'CI');
exec processa_grants_sistema(p_id_sistema => 'CE');
exec processa_grants_sistema(p_id_sistema => 'BS');
exec processa_grants_sistema(p_id_sistema => 'BG');
exec processa_grants_sistema(p_id_sistema => 'IS');
exec processa_grants_sistema(p_id_sistema => 'EX');
exec processa_grants_sistema(p_id_sistema => 'DB');
