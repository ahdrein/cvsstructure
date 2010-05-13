@define.sql
-- Conectar na base do INOUT para obter a data do inicio do processamento
conn &INOUT_USER/&INOUT_PASS@&TNS

spool arquivo_temporario.sql

SELECT 'spool instalacao_v01r01p00_txt_' || TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') || '.log' FROM DUAL;

spool off;

@arquivo_temporario.sql
@ordem_instalacao.sql

spool off;

