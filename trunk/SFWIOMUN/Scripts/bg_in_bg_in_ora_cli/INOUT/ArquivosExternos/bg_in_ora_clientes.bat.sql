conn &&INOUT_USER/&&INOUT_PASS@&&TNS

--  ///////
--  ///////     Script Gerado a partir do Sistema Gerenciador de Interfaces IN-OUT
--  ///////     Arquivo Externo: BG_IN_ORA_CLIENTES.BAT
--  ///////

set define off



delete from ARQUIVO_EXTERNO where NOME_ARQUIVO = 'BG_IN_ORA_CLIENTES.BAT';

insert into ARQUIVO_EXTERNO
(NOME_ARQUIVO, DESCRICAO, PATH_RELATIVO, CONTEUDO)
values
('BG_IN_ORA_CLIENTES.BAT','BG_IN_ORA_CLIENTES.BAT','INTERFACES','' || chr(64) || 'echo off' || CHR(13) || CHR(10));

exec CONCATENA_CONTEUDO ('BG_IN_ORA_CLIENTES.BAT','rem &1 = Id da Importacao' || CHR(13) || CHR(10));

exec CONCATENA_CONTEUDO ('BG_IN_ORA_CLIENTES.BAT','rem &2 = Usuario' || CHR(13) || CHR(10));

exec CONCATENA_CONTEUDO ('BG_IN_ORA_CLIENTES.BAT','rem &3 = Password' || CHR(13) || CHR(10));

exec CONCATENA_CONTEUDO ('BG_IN_ORA_CLIENTES.BAT','rem &4 = TNS' || CHR(13) || CHR(10));

exec CONCATENA_CONTEUDO ('BG_IN_ORA_CLIENTES.BAT','rem &5 = Quantidade de Registros a processar' || CHR(13) || CHR(10));

exec CONCATENA_CONTEUDO ('BG_IN_ORA_CLIENTES.BAT','rem &6 = Id do Evento (EVENT_TYPE_ID)' || CHR(13) || CHR(10));

exec CONCATENA_CONTEUDO ('BG_IN_ORA_CLIENTES.BAT','rem &7 = Reprocessa Erro (0 = (1); 1 = (1,4); 2 = (4)) ' || CHR(13) || CHR(10));

exec CONCATENA_CONTEUDO ('BG_IN_ORA_CLIENTES.BAT','' || CHR(13) || CHR(10));

exec CONCATENA_CONTEUDO ('BG_IN_ORA_CLIENTES.BAT','sqlplus -s /nolog ' || chr(64) || 'INTERFACES\EBS_PROCESSA_INTERFACE_ENTRADA.SQL %1 %2 %3 %4 1000 38 1' || CHR(13) || CHR(10));

exec CONCATENA_CONTEUDO ('BG_IN_ORA_CLIENTES.BAT','sqlplus -s /nolog ' || chr(64) || 'INTERFACES\EBS_PROCESSA_INTERFACE_ENTRADA.SQL %1 %2 %3 %4 1000 39 1' || CHR(13) || CHR(10));

exec CONCATENA_CONTEUDO ('BG_IN_ORA_CLIENTES.BAT','sqlplus -s /nolog ' || chr(64) || 'INTERFACES\EBS_PROCESSA_INTERFACE_ENTRADA.SQL %1 %2 %3 %4 1000 40 1' || CHR(13) || CHR(10));

exec CONCATENA_CONTEUDO ('BG_IN_ORA_CLIENTES.BAT','' || CHR(13) || CHR(10));

exec CONCATENA_CONTEUDO ('BG_IN_ORA_CLIENTES.BAT','sqlplus -s %2/%3' || chr(64) || '%4 ' || chr(64) || 'PROCESSA_INTERFACE.SQL %1' || CHR(13) || CHR(10));

commit;



set define on

-- //////
-- //////  Fim do Script
-- //////