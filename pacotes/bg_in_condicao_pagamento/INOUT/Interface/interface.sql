conn &&INOUT_USER/&&INOUT_PASS@&&TNS
--  ///////
--  ///////     Script Gerado a partir do Sistema Gerenciador de Interfaces IN-OUT
--  ///////     Interface: BG - IN - TXT - Condição de Pagamento
--  ///////


delete from PERMISSAO_TABELA where ID_INTERFACE = 'BG_IN_COND_PAGT';

delete from SISTEMA_INTERFACE where ID_INTERFACE = 'BG_IN_COND_PAGT';


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
      ('BG_IN_COND_PAGT',
      'BG - IN - TXT - Condição de Pagamento',
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
                DESCRICAO = 'BG - IN - TXT - Condição de Pagamento',
                NOME_MAQUINA = '&&USER_MACHINE',
                EXECUTAVEL = 'IMPORTADOR_IN_OUT_NEW.EXE',
                USERNAME = 'ADM',
                TIPO_INTERFACE = 'E',
                INTERFERE_PROC_DIR = 'N'
        where id_interface  = 'BG_IN_COND_PAGT';
end;
/


insert into PERMISSAO_TABELA (ID_INTERFACE, TABLE_NAME) values ('BG_IN_COND_PAGT', 'BG_CONDICAO_PAGAMENTO' ); 

insert into PERMISSAO_TABELA (ID_INTERFACE, TABLE_NAME) values ('BG_IN_COND_PAGT', 'BG_CONDICAO_PAGAMENTO_DIA' ); 

insert into PERMISSAO_TABELA (ID_INTERFACE, TABLE_NAME) values ('BG_IN_COND_PAGT', 'BG_CONDICAO_PAGAMENTO_IDIOMA' ); 

insert into PERMISSAO_TABELA (ID_INTERFACE, TABLE_NAME) values ('BG_IN_COND_PAGT', 'BG_CONDICAO_PAGAMENTO_SISTEMA' ); 

delete from INTERFACE_SAIDA where ID_INTERFACE =  'BG_IN_COND_PAGT';

insert into SISTEMA_INTERFACE (ID_INTERFACE, ID_SISTEMA, PRIORIDADE) values ('BG_IN_COND_PAGT', 'BG', 0 ); 

commit;


-- //////
-- //////  FIM DO SCRIPT
-- //////

