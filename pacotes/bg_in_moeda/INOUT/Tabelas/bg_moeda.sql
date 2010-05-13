conn &&INOUT_USER/&&INOUT_PASS@&&TNS
--  ///////
--  ///////     Script Gerado a partir do Sistema Gerenciador de Interfaces IN-OUT
--  ///////     TABELA: BG_MOEDA
--  ///////

delete from DEPEND_HEADER_ITEM_TMP;

delete from COLUNAS_TAB_INTERFACE where TABLE_NAME = 'BG_MOEDA';

begin

  update	TAB_INTERFACE             
  set		DESCRICAO = '',
		CTL_NAME = 'BG_MOEDA', 
		PREFIX_FILE = 'MOEDAH', 
		GERAR_CTL = 'S', 
		PRIORIDADE = 0, 
		ODBC_SOURCE_NAME = '', 
		ODBC_USER = '', 
		ODBC_PASSWORD = '', 
		ODBC_TABLE_NAME = '', 
		ODBC_WHERE = '', 
		ODBC_SELECT_ESPECIFICO = '', 
		TIPO_INTERFACE = 'LOADER',
		ORACLE_INITIAL_EXTENT = '', 
		ORACLE_NEXT_EXTENT = '', 
		ORACLE_INDEX_TABLESPACE = '',
		ELIMINAR_REG_EXECUCAO = 'N',
		ID_SISTEMA = 'BG',
		PROCEDURE_NAME = 'PKG_SFW_MOEDA.PRC_MOEDA',
		SEPARADOR = '',
		CTL_FIXO = null,
		TRIGGER1 = null,
		TRIGGER2 = null,
		COMANDO_EXTRA_LOADER = ''
  where TABLE_NAME = 'BG_MOEDA';

  if SQL%notfound then
		insert into TAB_INTERFACE 
		(TABLE_NAME,
		DESCRICAO, 
		CTL_NAME, 
		PREFIX_FILE, 
		GERAR_CTL, 
		PRIORIDADE, 
		ODBC_SOURCE_NAME, 
		ODBC_USER, 
		ODBC_PASSWORD, 
		ODBC_TABLE_NAME, 
		ODBC_WHERE, 
		ODBC_SELECT_ESPECIFICO, 
		TIPO_INTERFACE, 
		ORACLE_INITIAL_EXTENT, 
		ORACLE_NEXT_EXTENT, 
		ORACLE_INDEX_TABLESPACE, 
		ELIMINAR_REG_EXECUCAO, 
		COMANDO_EXTRA_LOADER, 
		ID_SISTEMA, 
		PROCEDURE_NAME, 
		SEPARADOR)
		values
		('BG_MOEDA',
		'',
		'BG_MOEDA', 
		'MOEDAH', 
		'S', 
		0, 
		'', 
		'', 
		'', 
		'', 
		'', 
		'', 
		'LOADER', 
		'', 
		'', 
		'',
		'N',
		'',
		'BG',
		'PKG_SFW_MOEDA.PRC_MOEDA',
		'');
  end if;
end;
/


insert into COLUNAS_TAB_INTERFACE
(TABLE_NAME, COLUMN_NAME, TIPO_LOADER, FORMATO, COMANDO_EXTRA, TAMANHO, ORDEM, DESCRICAO, ARG_NAME, ARG_FUNCTION)
values
('BG_MOEDA', 'COD_MOEDA', 'CHAR', '', '', '25', 
'1','Código da Moeda  Referência: Tabela_Moeda','P_COD_MOEDA','');

insert into COLUNAS_TAB_INTERFACE
(TABLE_NAME, COLUMN_NAME, TIPO_LOADER, FORMATO, COMANDO_EXTRA, TAMANHO, ORDEM, DESCRICAO, ARG_NAME, ARG_FUNCTION)
values
('BG_MOEDA', 'DESC_MOEDA', 'CHAR', '', '', '60', 
'2','Descrição','P_DESC_MOEDA','');

insert into COLUNAS_TAB_INTERFACE
(TABLE_NAME, COLUMN_NAME, TIPO_LOADER, FORMATO, COMANDO_EXTRA, TAMANHO, ORDEM, DESCRICAO, ARG_NAME, ARG_FUNCTION)
values
('BG_MOEDA', 'COD_MOEDA_SISCOMEX', 'CHAR', '', '', '3', 
'3','Código da Moeda no Siscomex','P_COD_MOEDA_SISCOMEX','');

insert into COLUNAS_TAB_INTERFACE
(TABLE_NAME, COLUMN_NAME, TIPO_LOADER, FORMATO, COMANDO_EXTRA, TAMANHO, ORDEM, DESCRICAO, ARG_NAME, ARG_FUNCTION)
values
('BG_MOEDA', 'SIMBOLO_MOEDA', 'CHAR', '', '', '20', 
'4','Simbolo da moeda.','P_SIMBOLO_MOEDA','');

insert into COLUNAS_TAB_INTERFACE
(TABLE_NAME, COLUMN_NAME, TIPO_LOADER, FORMATO, COMANDO_EXTRA, TAMANHO, ORDEM, DESCRICAO, ARG_NAME, ARG_FUNCTION)
values
('BG_MOEDA', 'REFERENCIA', 'CHAR', '', '', '1', 
'5','Referencia','P_REFERENCIA','');

insert into COLUNAS_TAB_INTERFACE
(TABLE_NAME, COLUMN_NAME, TIPO_LOADER, FORMATO, COMANDO_EXTRA, TAMANHO, ORDEM, DESCRICAO, ARG_NAME, ARG_FUNCTION)
values
('BG_MOEDA', 'ULTIMO_INFORMANTE', 'CHAR', '', '', '47', 
'6','Ultimo informante','P_ULTIMO_INFORMANTE','');

insert into COLUNAS_TAB_INTERFACE
(TABLE_NAME, COLUMN_NAME, TIPO_LOADER, FORMATO, COMANDO_EXTRA, TAMANHO, ORDEM, DESCRICAO, ARG_NAME, ARG_FUNCTION)
values
('BG_MOEDA', 'ATUALIZACAO', 'CHAR', '', '', '14', 
'7','Data de atualização Formato: YYYYMMDDHHMMSS','P_ATUALIZACAO','');

insert into COLUNAS_TAB_INTERFACE
(TABLE_NAME, COLUMN_NAME, TIPO_LOADER, FORMATO, COMANDO_EXTRA, TAMANHO, ORDEM, DESCRICAO, ARG_NAME, ARG_FUNCTION)
values
('BG_MOEDA', 'LEITURA_PTAX', 'CHAR', '', '', '1', 
'8','Indica se a moeda deve ter a taxa de paridade carregada pelo Cambio Sys','P_LEITURA_PTAX','');

insert into COLUNAS_TAB_INTERFACE
(TABLE_NAME, COLUMN_NAME, TIPO_LOADER, FORMATO, COMANDO_EXTRA, TAMANHO, ORDEM, DESCRICAO, ARG_NAME, ARG_FUNCTION)
values
('BG_MOEDA', 'TIPO_MOEDA', 'CHAR', '', '', '1', 
'9','Define como será a conversão de uma moeda estrangeira para dólares dos Estados Unidos (A ou B). Default NULL.','P_TIPO_MOEDA','');

commit;

set serveroutput on
exec PRC_SINCRONIZA_TABELA('BG_MOEDA');

CREATE OR REPLACE
TRIGGER TI_BG_MOEDA
BEFORE INSERT ON BG_MOEDA
REFERENCING OLD AS old NEW AS new
FOR EACH ROW
begin
	select seq_id_interface.nextval into :new.id from dual;
 
end;
/


CREATE OR REPLACE
TRIGGER TIA_BG_MOEDA
AFTER INSERT ON BG_MOEDA
REFERENCING OLD AS old NEW AS new
FOR EACH ROW
begin
	insert into REGISTROS_INTERFACES (ID, ID_IMPORTACAO, TABLE_NAME, ID_REF)
	values (:new.id, :new.id_importacao, 'BG_MOEDA', :new.id_ref);
end;
/


CREATE OR REPLACE
TRIGGER TD_BG_MOEDA
BEFORE DELETE ON BG_MOEDA
REFERENCING OLD AS old NEW AS new
FOR EACH ROW
begin
	delete from REGISTROS_INTERFACES where id = :old.id;
end;
/


 
-- //////
-- //////  Não esqueça de reprocessar todos os GRANTS para todos os sistemas
-- //////

