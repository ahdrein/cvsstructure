conn &&INOUT_USER/&&INOUT_PASS@&&TNS

--  ///////
--  ///////     Script Gerado a partir do Sistema Gerenciador de Interfaces IN-OUT
--  ///////     Interface: BG - IN - ORA - Clientes
--  ///////


delete from PERMISSAO_TABELA where ID_INTERFACE = 'BG_IN_ORA_CLI';

delete from SISTEMA_INTERFACE where ID_INTERFACE = 'BG_IN_ORA_CLI';

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
      ('BG_IN_ORA_CLI',
      'BG - IN - ORA - Clientes',
   	  '&&USER_MACHINE',
	  'INTERFACES\BG_IN_ORA_CLIENTES.BAT',
	  'ADM',
	  '0',
   'E'
      ,'N'
);
exception
    when dup_val_on_index then
        update INTERFACES
        set
                DESCRICAO = 'BG - IN - ORA - Clientes',
                NOME_MAQUINA = '&&USER_MACHINE',
                EXECUTAVEL = 'INTERFACES\BG_IN_ORA_CLIENTES.BAT',
                USERNAME = 'ADM',
                TIPO_INTERFACE = 'E'
        where id_interface  = 'BG_IN_ORA_CLI';
end;
/

insert into permissao_tabela
(ID_INTERFACE,TABLE_NAME) 
values  
('BG_IN_ORA_CLI','BG_PARCEIRO');

insert into permissao_tabela
(ID_INTERFACE,TABLE_NAME) 
values  
('BG_IN_ORA_CLI','BG_PARCEIRO_COMPLEMENTO');

insert into permissao_tabela
(ID_INTERFACE,TABLE_NAME) 
values  
('BG_IN_ORA_CLI','BG_PARCEIRO_CONTATO');

insert into permissao_tabela
(ID_INTERFACE,TABLE_NAME) 
values  
('BG_IN_ORA_CLI','BG_PARCEIRO_FUNCAO');

insert into permissao_tabela
(ID_INTERFACE,TABLE_NAME) 
values  
('BG_IN_ORA_CLI','BG_PARCEIRO_INFORMANTE');

insert into permissao_tabela
(ID_INTERFACE,TABLE_NAME) 
values  
('BG_IN_ORA_CLI','BG_PARCEIRO_ORGANIZACAO');

delete from INTERFACE_SAIDA where ID_INTERFACE =  'BG_IN_ORA_CLI';

insert into SISTEMA_INTERFACE (ID_INTERFACE, ID_SISTEMA) values ('BG_IN_ORA_CLI' , 'BG');

commit;


-- //////
-- //////  FIM DO SCRIPT
-- //////