conn &&INOUT_USER/&&INOUT_PASS@&&TNS
--  ///////
--  ///////     Script Gerado a partir do Sistema Gerenciador de Interfaces IN-OUT
--  ///////     Interface: BG - IN - TXT - Moedas
--  ///////


delete from PERMISSAO_TABELA where ID_INTERFACE = 'SFW_MO';

delete from SISTEMA_INTERFACE where ID_INTERFACE = 'SFW_MO';


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
      ('SFW_MO',
      'BG - IN - TXT - Moedas',
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
                DESCRICAO = 'BG - IN - TXT - Moedas',
                NOME_MAQUINA = '&&USER_MACHINE',
                EXECUTAVEL = 'IMPORTADOR_IN_OUT_NEW.EXE',
                USERNAME = 'ADM',
                TIPO_INTERFACE = 'E',
                INTERFERE_PROC_DIR = 'N'
        where id_interface  = 'SFW_MO';
end;
/


insert into PERMISSAO_TABELA (ID_INTERFACE, TABLE_NAME) values ('SFW_MO', 'BG_MOEDA' ); 

insert into PERMISSAO_TABELA (ID_INTERFACE, TABLE_NAME) values ('SFW_MO', 'BG_MOEDA_CONVERSAO' ); 

insert into PERMISSAO_TABELA (ID_INTERFACE, TABLE_NAME) values ('SFW_MO', 'BG_MOEDA_SISCOMEX' ); 

insert into PERMISSAO_TABELA (ID_INTERFACE, TABLE_NAME) values ('SFW_MO', 'BG_MOEDA_TAXA' ); 

insert into PERMISSAO_TABELA (ID_INTERFACE, TABLE_NAME) values ('SFW_MO', 'BG_MOEDA_TRADUCAO' ); 

delete from INTERFACE_SAIDA where ID_INTERFACE =  'SFW_MO';

insert into SISTEMA_INTERFACE (ID_INTERFACE, ID_SISTEMA, PRIORIDADE) values ('SFW_MO', 'BG', 0 ); 

commit;


-- //////
-- //////  FIM DO SCRIPT
-- //////

