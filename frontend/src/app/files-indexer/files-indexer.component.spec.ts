import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FilesIndexerComponent } from './files-indexer.component';

describe('FilesIndexerComponent', () => {
  let component: FilesIndexerComponent;
  let fixture: ComponentFixture<FilesIndexerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FilesIndexerComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FilesIndexerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
