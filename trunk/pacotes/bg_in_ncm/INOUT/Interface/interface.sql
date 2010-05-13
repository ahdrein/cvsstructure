conn &&INOUT_USER/&&INOUT_PASS@&&TNS
--  ///////
--  ///////     Script Gerado a partir do Sistema Gerenciador de Interfaces IN-OUT
--  ///////     Interface: BG - IN - TXT - NCM
--  ///////


delete from PERMISSAO_TABELA where ID_INTERFACE = 'SFW_NCM';

delete from SISTEMA_INTERFACE where ID_INTERFACE = 'SFW_NCM';


begin
   insert into    INTERFACES
      (ID_INTERFACE,
      DESCRICAO,
      NOME_MAQUINA,
      EXECUTAVEL,
      USERNAME,
      TEMPO_MEDIO,
      TIPO_INTERFACE,
      INTERFERE_PROC_DIR)
      values
      ('SFW_NCM',
      'BG - IN - TXT - NCM',
      '&&USER_MACHINE',
      'IMPORTADOR_IN_OUT_NEW.EXE',
      'ADM',
      '0',
      'E',
      'N');
exception
    when dup_val_on_index then
        update INTERFACES
        set
                DESCRICAO = 'BG - IN - TXT - NCM',
                NOME_MAQUINA = '&&USER_MACHINE',
                EXECUTAVEL = 'IMPORTADOR_IN_OUT_NEW.EXE',
                USERNAME = 'ADM',
                TIPO_INTERFACE = 'E',
                INTERFERE_PROC_DIR = 'N'
        where id_interface  = 'SFW_NCM';
end;
/


insert into PERMISSAO_TABELA (ID_INTERFACE, TABLE_NAME) values ('SFW_NCM', 'BG_DESTAQUE_NCM' ); 

insert into PERMISSAO_TABELA (ID_INTERFACE, TABLE_NAME) values ('SFW_NCM', 'BG_NCM_SISCOMEX' ); 

delete from INTERFACE_SAIDA where ID_INTERFACE =  'SFW_NCM';

insert into SISTEMA_INTERFACE (ID_INTERFACE, ID_SISTEMA, PRIORIDADE) values ('SFW_NCM', 'BG', 0 ); 

commit;


-- //////
-- //////  FIM DO SCRIPT
-- //////

