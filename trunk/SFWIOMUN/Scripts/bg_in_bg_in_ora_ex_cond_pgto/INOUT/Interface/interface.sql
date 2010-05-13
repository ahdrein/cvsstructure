conn &&INOUT_USER/&&INOUT_PASS@&&TNS

--  ///////
--  ///////     Script Gerado a partir do Sistema Gerenciador de Interfaces IN-OUT
--  ///////     Interface: BG - IN - ORA - EX - Condições de Pagamentos de Exportação
--  ///////


delete from PERMISSAO_TABELA where ID_INTERFACE = 'BG_IN_ORA_EX_COND_PGTO';

delete from SISTEMA_INTERFACE where ID_INTERFACE = 'BG_IN_ORA_EX_COND_PGTO';

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
      ('BG_IN_ORA_EX_COND_PGTO',
      'BG - IN - ORA - EX - Condições de Pagamentos de Exportação',
   	  '&&USER_MACHINE',
	  'INTERFACES\BG_IN_ORA_COND_PGTO_EXP.BAT',
	  'ADM',
	  '0',
   'E'
      ,'N'
);
exception
    when dup_val_on_index then
        update INTERFACES
        set
                DESCRICAO = 'BG - IN - ORA - EX - Condições de Pagamentos de Exportação',
                NOME_MAQUINA = '&&USER_MACHINE',
                EXECUTAVEL = 'INTERFACES\BG_IN_ORA_COND_PGTO_EXP.BAT',
                USERNAME = 'ADM',
                TIPO_INTERFACE = 'E'
        where id_interface  = 'BG_IN_ORA_EX_COND_PGTO';
end;
/

insert into permissao_tabela
(ID_INTERFACE,TABLE_NAME) 
values  
('BG_IN_ORA_EX_COND_PGTO','BG_CONDICAO_PAGAMENTO');

insert into permissao_tabela
(ID_INTERFACE,TABLE_NAME) 
values  
('BG_IN_ORA_EX_COND_PGTO','BG_CONDICAO_PAGAMENTO_DIA');

insert into permissao_tabela
(ID_INTERFACE,TABLE_NAME) 
values  
('BG_IN_ORA_EX_COND_PGTO','BG_CONDICAO_PAGAMENTO_IDIOMA');

insert into permissao_tabela
(ID_INTERFACE,TABLE_NAME) 
values  
('BG_IN_ORA_EX_COND_PGTO','BG_CONDICAO_PAGAMENTO_SISTEMA');

delete from INTERFACE_SAIDA where ID_INTERFACE =  'BG_IN_ORA_EX_COND_PGTO';

insert into SISTEMA_INTERFACE (ID_INTERFACE, ID_SISTEMA) values ('BG_IN_ORA_EX_COND_PGTO' , 'BG');

commit;


-- //////
-- //////  FIM DO SCRIPT
-- //////