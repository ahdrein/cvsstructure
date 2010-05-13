conn &&INOUT_USER/&&INOUT_PASS@&&TNS
--  ///////
--  ///////     Script Gerado a partir do Sistema Gerenciador de Interfaces IN-OUT
--  ///////     Interface: BG - IN - TXT - Embalagens

--  ///////


delete from PERMISSAO_TABELA where ID_INTERFACE = 'SFW_EMB';

delete from SISTEMA_INTERFACE where ID_INTERFACE = 'SFW_EMB';


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
      ('SFW_EMB',
      'BG - IN - TXT - Embalagens',
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
                DESCRICAO = 'BG - IN - TXT - Embalagens',
                NOME_MAQUINA = '&&USER_MACHINE',
                EXECUTAVEL = 'IMPORTADOR_IN_OUT_NEW.EXE',
                USERNAME = 'ADM',
                TIPO_INTERFACE = 'E',
                INTERFERE_PROC_DIR = 'N'
        where id_interface  = 'SFW_EMB';
end;
/


insert into PERMISSAO_TABELA (ID_INTERFACE, TABLE_NAME) values ('SFW_EMB', 'BG_EMBALAGEM_TIPO' ); 

insert into PERMISSAO_TABELA (ID_INTERFACE, TABLE_NAME) values ('SFW_EMB', 'BG_EMBALAGEM_TIPO_IDIOMA' ); 

delete from INTERFACE_SAIDA where ID_INTERFACE =  'SFW_EMB';

insert into SISTEMA_INTERFACE (ID_INTERFACE, ID_SISTEMA, PRIORIDADE) values ('SFW_EMB', 'BG', 0 ); 

commit;


-- //////
-- //////  FIM DO SCRIPT
-- //////

